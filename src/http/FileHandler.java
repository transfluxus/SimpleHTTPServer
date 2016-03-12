package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import processing.core.PApplet;

/**
 * Serves as basic class for SimpleFileHandler and TemplateFileHandler
 * 
 * @author raminsoleymani
 *
 */
abstract class FileHandler extends SHTTPSHandler {

	protected String fileName;
	protected File file;
	protected String contentType;

	protected static Logger logger = Logger.getLogger("server");

	public static PApplet parent;

	protected FileHandler() {
		// this is for preventing to have a Try/Catch block for
		// TemplateFileHandler
	}

	public FileHandler(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		this.file = getFile(fileName);
	}

	protected File getFile(String fileName) throws FileNotFoundException {
		File file = new File(SimpleHTTPServer.parent.sketchPath() + "/data/" + fileName);
		if (file.exists()) {
			return new File(SimpleHTTPServer.parent.sketchPath() + "/data/" + fileName);
		} else {
			file = new File(fileName);
			if (!file.exists()) {
				Logger.getLogger(" ").warning("Cannot create FileHandler: " + fileName + " is missing");
				throw new FileNotFoundException("Cannot create FileHandler: " + fileName + " is missing");
			}
			return file;
		}
	}

	protected static String getContentType(String fileName) {
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
			logger.warning(fileName
					+ ": content type could not be derived. Better use SimpleFileHandler(String fileName, String contentType)");
			return "text/html";
		}
	}

	abstract protected byte[] getResponseBytes();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		setHttpExchange(exchange);
		byte[] response = getResponseBytes();
		if (contentType != null) {
			Headers headers = exchange.getResponseHeaders();
			headers.add("Content-Type", contentType);
		}
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response, 0, response.length);
		os.close();
	}

	protected void printExchange(HttpExchange exchange) {
		System.out.println("context path: " + exchange.getHttpContext().getPath());
		System.out.println("protocol: " + exchange.getProtocol());
		Map<String, Object> attributes = exchange.getHttpContext().getAttributes();
		System.out.println("# attribtues: " + attributes.size());
		for (Iterator<String> iter = attributes.keySet().iterator(); iter.hasNext();) {
			String attribute = iter.next();
			System.out.println(attribute + "," + exchange.getAttribute(attribute));
		}
		System.out.println("request method: " + exchange.getRequestMethod());
		System.out.println("response code: " + exchange.getResponseCode());
		System.out.println("local address: " + exchange.getLocalAddress());
		System.out.println("principal: " + exchange.getPrincipal());
		System.out.println("remote address: " + exchange.getRemoteAddress());
		System.out.println("request headers: " + exchange.getRequestHeaders().size());
		for (Iterator<String> iter = exchange.getRequestHeaders().keySet().iterator(); iter.hasNext();) {
			String attribute = iter.next();
			System.out.println(attribute + "," + exchange.getAttribute(attribute));
		}
		System.out.println("request uri: " + exchange.getRequestURI());
	}
}
