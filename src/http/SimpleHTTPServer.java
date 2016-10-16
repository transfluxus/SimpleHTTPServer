package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import processing.core.PApplet;

/**
 * The baseclass. That's the server.
 * 
 * @author raminsoleymani
 *
 */
public class SimpleHTTPServer {

	protected static PApplet parent;
	private HttpServer server;

	SimpleFileHandler indexFileHandler;

	boolean isRunning;

	public static Configuration freeMarker_configuration;

	protected static Logger logger = Logger.getLogger("server");

	public static boolean useIndexHtml = true;

	private List<HttpContext> contextList = new ArrayList<>();

	static {
		logger.getParent().getHandlers()[0].setFormatter(new SimpleFormatter());
		// logger.getParent().getHandlers()[0].setLevel(Level.FINEST);
	}

	/**
	 * Creates a HTTPServer listening on port 8000
	 * 
	 * @param parent
	 *            Processing parent
	 */
	public SimpleHTTPServer(PApplet parent) {
		this(parent, 8000);
	}

	/**
	 * Creates a SimpleHTTPServer listening on a specified port
	 * 
	 * @param parent
	 *            Processing parent
	 * @param port
	 *            port for the server
	 */
	public SimpleHTTPServer(PApplet parent, int port) {
		SimpleHTTPServer.parent = parent;
		FileHandler.parent = parent;
		if (logger.getLevel() == null) {
			logger.setLevel(Level.WARNING);
		}
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (Exception exc) {
			logger.severe("Server couldn't be created: " + exc.getMessage());
			return;
		}
		if (useIndexHtml) {
			try {
				indexFileHandler = new SimpleFileHandler("index.html");
				createContext("", indexFileHandler);
			} catch (FileNotFoundException e) {
				logger.warning("You are not providing an index.html. Naughty naughty."
						+ System.getProperty("line.separator")
						+ "You might wanna use 'SimpleHTTPServer.useIndexHtml = false;' before creating this instance");
			}
		}
		server.start();
		isRunning = true;
		logger.setLevel(Level.INFO);
		logger.info("SimpleHTTPServer running on port " + port);
		// logger.getParent().getHandlers()[0].setFormatter(new
		// SimpleFormatter());
	}

	/**
	 * Starts the server
	 */
	public void start() {
		server.start();
	}

	/**
	 * Stop the server
	 */
	public void stop() {
		server.stop(0);
		isRunning = false;
	}

	/**
	 * Makes a file available on the server(html,css or js). the file url is
	 * /fileName
	 * 
	 * @param fileName
	 *            file in the data folder to serve
	 */
	public void serve(String fileName) {
		serve(fileName, fileName);
	}

	/**
	 * Makes a file available on the server(html,css or js)
	 * 
	 * @param path
	 *            url path
	 * @param fileName
	 *            file in the data folder to serve
	 */
	public void serve(String path, String fileName) {
		try {
			SimpleFileHandler fileHandler = new SimpleFileHandler(fileName);
			HttpContext context = server.createContext("/" + path, fileHandler);
			this.contextList.add(context);
			logger.info("Serving: " + fileName + " @ " + path);
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * Serve all files in the data folder, recursively, leaving out hidden
	 * files. Subfolders are also reflected in the context path. e.g. the path
	 * is "". Some file at data/subDir/x.html will have the path subDir/x.html
	 * 
	 * @param path
	 *            the context base-path
	 */
	public void serveAll(String path) {
		serveAll(path, parent.sketchPath() + "/data/", true, true);
	}

	/**
	 * Serves all files in the data folder. Subfolders are also reflected in the
	 * context path. e.g. the path is "". Some file at data/subDir/x.html will
	 * have the path subDir/x.html
	 * 
	 * @param path
	 *            the context base-path
	 * @param recursive
	 *            recursively include subdirectories
	 * @param ignoreHiddenFiles
	 *            ignore hidden files (starting with . under UNIX)
	 */
	public void serveAll(String path, boolean recursive, boolean ignoreHiddenFiles) {
		serveAll(path, parent.sketchPath() + "/data/", recursive, ignoreHiddenFiles);
	}

	/**
	 * Serves all files in the given folder. Subfolders are also reflected in
	 * the context path. e.g. the path is "". Some file at
	 * directoyName/subDir/x.html will have the path subDir/x.html
	 * 
	 * @param path
	 *            the context base-path
	 * @param directoyName
	 *            absolute directory of the files to serve
	 */
	public void serveAll(String path, String directoyName) {
		serveAll(path, directoyName, true, true);
	}

	/**
	 * Serves all files in the given folder. Subfolders are also reflected in
	 * the context path. e.g. the path is "". Some file at
	 * directoyName/subDir/x.html will have the path subDir/x.html
	 * 
	 * @param path
	 *            the context base-path
	 * @param directoyName
	 *            absolute directory of the files to serve
	 * @param recursive
	 *            recursively include subdirectories
	 * @param ignoreHiddenFiles
	 *            ignore hidden files (starting with . under UNIX)
	 */
	public void serveAll(String path, String directoyName, boolean recursive, boolean ignoreHiddenFiles) {
		File folder = new File(directoyName);
		if (!folder.isDirectory()) {
			logger.warning("serveAll: " + directoyName + " is not a directory");
			return;
		}
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				if (recursive) {
					serveAll(f.getName() + "/", f.getAbsolutePath(), recursive, ignoreHiddenFiles);
				}
			} else {
				if (!f.isHidden() || !ignoreHiddenFiles) {
					serve(path + f.getName(), f.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Makes a file available on the server(html,css or js)
	 * 
	 * @param path
	 *            url path
	 * @param fileName
	 *            file in the data folder to serve
	 * @param callbackFunctionName
	 *            name of the function
	 */
	public void serve(String path, String fileName, String callbackFunctionName) {
		try {
			SimpleFileHandler fileHandler = new SimpleFileHandler(fileName);
			HttpContext context = server.createContext("/" + path, fileHandler);
			this.contextList.add(context);
			logger.info("Serving: " + fileName + " ");
			Method callbackMethod = getCallbackMethod(callbackFunctionName);
			if (callbackMethod != null) {
				fileHandler.setCallbackMethod(callbackMethod);
			}
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * Makes SOMETHING available on the server und a sepecific url. Add any
	 * HTTPHandler
	 * 
	 * @param path
	 *            url
	 * @param handler
	 *            HttpHandler
	 *            (http://docs.oracle.com/javase/7/docs/api/javax/xml/ws/spi/
	 *            http/HttpHandler.html)
	 */
	public void createContext(String path, HttpHandler handler) {
		try {
			HttpContext context = server.createContext("/" + path, handler);
			this.contextList.add(context);
		} catch (Exception exc) {
			logger.warning("Context couldn't be created. Server is probably not running");
			return;
		}
		if (logger.isLoggable(Level.INFO)) {
			if (handler.getClass() == SimpleFileHandler.class) {
				logger.info("Serving: " + ((SimpleFileHandler) handler).fileName + " on path: /" + path);
			} else if (handler.getClass().getSuperclass() == TemplateFileHandler.class) {
				logger.info("Serving template: " + ((TemplateFileHandler) handler).fileName + " on path: /" + path);
			} else if (handler.getClass() == DynamicResponseHandler.class
					|| handler.getClass().getSuperclass() == DynamicResponseHandler.class) {
				logger.info("Serving Dynamic response on path: /" + path);
			}
		}

	}

	private Method getCallbackMethod(String callbackFunctionName) {
		Class<? extends PApplet> clazz = parent.getClass();
		try {
			Method method = clazz.getDeclaredMethod(callbackFunctionName,
					new Class<?>[] { String.class, HashMap.class });
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("No such Method: " + callbackFunctionName + " in PApplet");
		}
		return null;
	}

	/**
	 * Return the PApplet object that contains the server
	 * 
	 * @return PApplet object
	 */
	public PApplet getParent() {
		return parent;
	}

	/**
	 * return if the server has been started
	 * 
	 * @return true if server has been started
	 */
	public boolean isRunning() {
		return isRunning;
	}

	static protected void setupFMConfig(String templateFolderName) {
		if (freeMarker_configuration == null) {
			freeMarker_configuration = new Configuration(Configuration.VERSION_2_3_22);
			try {
				freeMarker_configuration.setDirectoryForTemplateLoading(new File(templateFolderName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			freeMarker_configuration.setDefaultEncoding("UTF-8");
			freeMarker_configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}
	}

	/**
	 * Remove the contect given under the passed uri path
	 * 
	 * @param uriPath
	 *            the path of the context
	 */
	public void removeContext(String uriPath) {
		try {
			if (getContext("/" + uriPath).isPresent()) {
				server.removeContext("/" + uriPath);
				HttpContext context = getContext("/" + uriPath).get();
				contextList.remove(context);
				logger.config("Removing context for path: /" + uriPath);
			} else {
				throw new IllegalArgumentException("context doesn't exist");
			}
		} catch (IllegalArgumentException iaExc) {
			logger.warning(iaExc.getMessage());
			logger.warning("Context at path: /" + uriPath + " cannot be removed. Does the path exist?");
		}
	}

	private Optional<HttpContext> getContext(String uriPath) {
		for (HttpContext context : contextList) {
			if (context.getPath().equals(uriPath))
				return Optional.of(context);
		}
		return Optional.empty();
	}

	/**
	 * add a callback function to the context under the given uri path. The
	 * callback function must be public, of the PApplet object and needs to have
	 * the parameters: (String uri, HashMap<String, String> parameterMap)
	 * 
	 * @param uriPath
	 *            uri path of the context, which should have the callback
	 * @param callbackFunctionName
	 *            name of the callback function
	 */
	public void addCallback(String uriPath, String callbackFunctionName) {
		Optional<HttpContext> context = getContext("/" + uriPath);
		if (!context.isPresent()) {
			logger.warning("No context given for the path: " + uriPath);
			return;
		}
		HttpHandler handler = context.get().getHandler();
		if (!handler.getClass().equals(SimpleFileHandler.class)) {
			logger.warning("The context for path: " + uriPath + " doesn't provide a SimpleFileHandler. "
					+ "Other Handler (DynamicResponseHandler,TemplateFileHandler) "
					+ "have functions that are called when request come in");
		} else {
			addCallback((SimpleFileHandler) handler, callbackFunctionName);
		}
	}

	private void addCallback(SimpleFileHandler fileHandler, String callbackFunctionName) {
		Method callbackMethod = getCallbackMethod(callbackFunctionName);
		if (callbackMethod != null) {
			fileHandler.setCallbackMethod(callbackMethod);
		}
	}

	/**
	 * Sets the level of the Logger. Setting this to
	 * java.util.logging.Level.INFO outputs more information on the created
	 * services
	 * 
	 * @param level
	 *            new level for the logger
	 */
	public static void setLoggerLevel(Level level) {
		logger.setLevel(level);
	}

	public void printAllContexts() {
		for (HttpContext context : contextList) {
			String path = context.getPath();
			String contextDescr = context.getHandler().toString();
			System.out.println(path + " - " + contextDescr);
		}
	}
}
