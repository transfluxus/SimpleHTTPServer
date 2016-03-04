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

import processing.core.PApplet;

class SimpleFileHandler extends ExtHttpHandler {

	public static PApplet parent;

	private final String fileName;
	private final String contentType;
	private boolean callbackMethodSet;
	private Method callbackMethod;

	public SimpleFileHandler(String fileName) {
		if(!parent.dataFile(fileName).exists()) {
			System.out.println("Cannot create FileHandler: "+fileName +" is missing");
		}
		this.fileName = fileName;
		if (fileName.endsWith(".html")) {
			this.contentType = "text/html";
		} else if (fileName.endsWith(".css")) {
			//System.out.println(fileName+" type:CSS");
			this.contentType = "text/css";
		} else if (fileName.endsWith(".js")) {
			this.contentType = "text/javascript";
		} else {
			System.out
					.println("content type could not be derrived. Better use SimpleFileHandler(String fileName, String contentType)");
			this.contentType = "text/html";
		}
	}

	public SimpleFileHandler(String fileName, String contentType) {
		this.fileName = fileName;
		this.contentType = contentType;
	}

	private byte[] getResponseBytes() {
		File file = new File(SimpleHTTPServer.parent.sketchPath() + "/data/"
				+ fileName);
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

	public void handle(HttpExchange t) throws IOException {
		//PApplet.println("handle: "+ t.getRequestURI().toString());
		byte[] response = getResponseBytes();
		if (contentType != null) {
			Headers headers = t.getResponseHeaders();
			headers.add("Content-Type", contentType);
			//System.out.println("serving "+fileName +" as " +contentType);
		}
		t.sendResponseHeaders(200, response.length);
		OutputStream os = t.getResponseBody();
		os.write(response, 0, response.length);
		os.close();
		
		if(callbackMethodSet) {
			//PApplet.println("callback!");
			String uri = t.getRequestURI().toString();
			Map<String, String> map = queryToMap(t);
			//PApplet.println("map:",map);
			try {
				callbackMethod.invoke(parent, new Object[]{uri,map});
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