package http;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

class SimpleFileHandler extends FileHandler {


	private boolean callbackMethodSet;
	private Method callbackMethod;

	public SimpleFileHandler(String fileName) throws FileNotFoundException {
		this(fileName,getContentType(fileName));
	}

	public SimpleFileHandler(String fileName, String contentType) throws FileNotFoundException {
		super(fileName);
		this.contentType = contentType;
	}

	protected byte[] getResponseBytes() {
		byte[] bytearray = new byte[(int) file.length()];
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(bytearray, 0, bytearray.length);
			bis.close();
		} catch (IOException ioexc) {
			System.err.println("error reading file: " + fileName);
			System.err.println(ioexc.getMessage());
		}
		return bytearray;
	}

	public void setCallbackMethod(Method callbackMethod) {
		callbackMethodSet = true;
		this.callbackMethod = callbackMethod;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		super.handle(exchange);
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
}