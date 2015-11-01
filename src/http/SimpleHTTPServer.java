package http;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;

import processing.core.PApplet;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHTTPServer {

	public static PApplet parent;
	HttpServer server;
	
	SimpleFileHandler indexFileHandler;

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
			indexFileHandler = new SimpleFileHandler("index.html");
			createContext("", indexFileHandler);
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
	 * Makes a file available on the server(html,css or js).
	 * the file url  is /fileName
	 * @param fileName file in the data folder to serve
	 */
	public void serve(String fileName) {
		serve(fileName, fileName);
	}
	
	/**
	 * Makes a file available on the server(html,css or js)
	 * @param path url path
	 * @param fileName file in the data folder to serve
	 */
	public void serve(String path,String fileName) {
		server.createContext("/"+path, new SimpleFileHandler(fileName));
	}

	/**
	 * Makes a file available on the server(html,css or js)
	 * @param path url path
	 * @param fileName file in the data folder to serve
	 * @param callbackFunctionName name of the function
	 */
	public void serve(String path,String fileName, String callbackFunctionName) {
		Method callbackMethod = getCallbackMethod(callbackFunctionName);
		SimpleFileHandler handler = new SimpleFileHandler(fileName);
		if(callbackMethod != null)
			handler.setCallbackMethod(callbackMethod);
		server.createContext("/"+path, handler);
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

	public Method getCallbackMethod(String callbackFunctionName) {
		Class<? extends PApplet> clazz = parent.getClass();
		try {
			Method method = clazz.getDeclaredMethod(callbackFunctionName,new Class<?>[]{String.class,HashMap.class});
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("No such Method: "+callbackFunctionName +" in PApplet");
		}
		return null;
	}

	public void setIndexCallback(String callbackFunctionName) {
		Class<? extends PApplet> clazz = parent.getClass();
		try {
			Method method = clazz.getDeclaredMethod(callbackFunctionName,new Class<?>[]{String.class,HashMap.class});
			indexFileHandler.setCallbackMethod(method);
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("No such Method: "+callbackFunctionName +" in PApplet");
		}
	}

}
