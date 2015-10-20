package http;

import java.net.InetSocketAddress;

import processing.core.PApplet;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHTTPServer {

	public static PApplet parent;
	HttpServer server;

	/**
	 * Creates a HTTPServer listening on port 8000
	 * @param parent Processing parent
	 */
	public SimpleHTTPServer(PApplet parent) {
		this(parent, 8000);
	}

	/**
	 * Creates a SimpleHTTPServer listening on a specified port
	 * @param parent Processing parent
	 * @param port port for the server
	 */
	public SimpleHTTPServer(PApplet parent, int port) {
		SimpleHTTPServer.parent = parent;
		SimpleFileHandler.parent = parent;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			createContext("", new SimpleFileHandler("index.html"));
			server.start();
			System.out.println("SimpleHTTPServer running on port 8000");
		} catch (Exception exc) {
			System.out.println();
			System.out.println("Server couldn't start: " + exc.getMessage());
		}
	}

	/**
	 * Stop the server
	 */
	public void stop() {
		server.stop(0);
	}

	/**
	 * Makes a file available on the server(html,css or js)
	 * @param path url path
	 * @param fileName file in the data folder to serve
	 */
	public void serve(String path,String fileName) {
		server.createContext(path, new SimpleFileHandler(fileName));
	}
	
	/**
	 * Makes a file available on the server(html,css or js).
	 * the file url  is /fileName
	 * @param fileName file in the data folder to serve
	 */
	public void serve(String fileName) {
		server.createContext("/"+fileName, new SimpleFileHandler(fileName));
	}
	
	/**
	 * Makes SOMETHING available on the server und a sepecific url.
	 * Add any HTTPHandler
	 * @param fileName url
	 * @param handler HttpHandler (http://docs.oracle.com/javase/7/docs/api/javax/xml/ws/spi/http/HttpHandler.html)
	 */
	public void createContext(String fileName, HttpHandler handler) {
		server.createContext("/"+fileName, handler);
	}

}
