package http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import processing.core.PApplet;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class SimpleFileHandler implements HttpHandler {

	private final String fileName;
	private String contentType;
	public static PApplet parent;

	public SimpleFileHandler(String fileName) {
		if(!parent.dataFile(fileName).exists()) {
			System.out.println("Cannot create FileHandler: "+fileName +" is missing");
		}
		this.fileName = fileName;
		if (fileName.endsWith(".html")) {
			this.contentType = "text/html";
		} else if (fileName.endsWith(".css")) {
			this.contentType = "text/css";
		} else if (fileName.endsWith(".js")) {
			this.contentType = "text/javascript";
		} else {
			System.out
					.println("content type could not be derrived. Better use SimpleFileHandler(String fileName, String contentType)");
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
		// println(t.getRequestMethod()+" "+t.getRequestURI() );
		// for(String s : t.getRequestHeaders().keySet())
		// println("    ",s,t.getRequestHeaders().get(s));
		byte[] response = getResponseBytes();
		if (contentType != null) {
			Headers headers = t.getResponseHeaders();
			headers.add("Content-Type", contentType);
		}
		t.sendResponseHeaders(200, response.length);
		OutputStream os = t.getResponseBody();
		os.write(response, 0, response.length);
		os.close();
	}
}