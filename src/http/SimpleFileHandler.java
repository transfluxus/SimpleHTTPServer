package http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import freemarker.log.Logger;
import processing.core.PApplet;

class SimpleFileHandler extends ExtHttpHandler {

	public static PApplet parent;

	protected final String fileName;
	protected final File file;
	private final String contentType;
	private boolean callbackMethodSet;
	private Method callbackMethod;

	public SimpleFileHandler(String fileName) {
		this.fileName = fileName;
		this.file = getFile(fileName);
		this.contentType = getContentType(fileName);
	}

	private File getFile(String fileName) {
		if (parent.dataFile(fileName).exists()) {
			return new File(SimpleHTTPServer.parent.sketchPath() + "/data/" + fileName);
		} else {
			File file = new File(fileName);
			if (!file.exists()) {
				Logger.getLogger(" ").warn("Cannot create FileHandler: " + fileName + " is missing");
			}
			return file;
		}
	}
	
	private String getContentType(String fileName) {
		if (fileName.endsWith(".html")) {
			return "text/html";
		} else if (fileName.endsWith(".css")) {
			// System.out.println(fileName+" type:CSS");
			return "text/css";
		} else if (fileName.endsWith(".js")) {
			return "text/javascript";
		} else if (fileName.endsWith(".ftl")) {
			return "text/html";
		} else {
			System.out.println(
					"content type could not be derrived. Better use SimpleFileHandler(String fileName, String contentType)");
			return "text/html";
		}
	}

	public SimpleFileHandler(String fileName, String contentType) {
		this.fileName = fileName;
		this.file = getFile(fileName);
		this.contentType = getContentType(fileName);
	}

	protected byte[] getResponseBytes() {
		byte[] bytearray = new byte[(int) file.length()];
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);
			fis.close();
		} catch (IOException ioexc) {
			System.err.println("error reading file: " + fileName);
		}
		return bytearray;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		setHttpExchange(exchange);
		byte[] response = getResponseBytes();
		if (contentType != null) {
			Headers headers = exchange.getResponseHeaders();
			headers.add("Content-Type", contentType);
			// System.out.println("serving "+fileName +" as " +contentType);
		}
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response, 0, response.length);
		os.close();

		if (callbackMethodSet) {
			// PApplet.println("callback!");
			String uri = exchange.getRequestURI().toString();
			Map<String, String> map = queryToMap(exchange);
			// PApplet.println("map:",map);
			try {
				callbackMethod.invoke(parent, new Object[] { uri, map });
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void setCallbackMethod(Method callbackMethod) {
		callbackMethodSet = true;
		this.callbackMethod = callbackMethod;
	}

}