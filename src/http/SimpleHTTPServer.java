package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import processing.core.PApplet;

public class SimpleHTTPServer {

	protected static PApplet parent;
	private HttpServer server;

	SimpleFileHandler indexFileHandler;

	boolean isRunning;

	public static Configuration freeMarker_configuration;

	protected static Logger logger = Logger.getLogger("server");

	public static boolean useIndexHtml = true;
	
	static {
		logger.getParent().getHandlers()[0].setFormatter(new SimpleFormatter());
		logger.getParent().getHandlers()[0].setLevel(Level.FINEST);
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
			logger.setLevel(Level.INFO);
		}

		// logger.getHandlers()[0].setLevel(Level.FINEST);
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (Exception exc) {
			logger.severe("Server couldn't be created: " + exc.getMessage());
			logger.severe("Bye Bye!");
			System.exit(1);
		}
		try {
			indexFileHandler = new SimpleFileHandler("index.html");
		} catch (FileNotFoundException e) {
			logger.warning("You are not providing an index.html. Naughty naughty");
		}
		if(useIndexHtml) {
			createContext("", indexFileHandler);
		}
		server.start();
		isRunning = true;
		logger.info("SimpleHTTPServer running on port " + port);
		logger.getParent().getHandlers()[0].setFormatter(new SimpleFormatter());
	}

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
			server.createContext("/" + path, fileHandler);
			logger.info("Serving: "+fileName +" ");
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		}
	}

	public void serveAll(String path) {
		serveAll(path,parent.sketchPath() + "/data/", true, true);
	}

	public void serveAll(String path, boolean recursive, boolean ignoreHiddenFiles) {
		serveAll(path,parent.sketchPath() + "/data/", recursive, ignoreHiddenFiles);
	}
	
	/**
	 * Creates contexts for all files in the server recursively
	 * 
	 * @param folderName
	 */
	public void serveAll(String path, String directoyName) {
		serveAll(path,directoyName, true, true);
	}

	public void serveAll(String path, String directoyName, boolean recursive, boolean ignoreHiddenFiles) {
		File folder = new File(directoyName);
		if(!folder.isDirectory()) {
			logger.warning("serveAll: "+directoyName +" is not a directory");
			return;
		} 
		File[] files = folder.listFiles();
		for(File f : files) {
			if(f.isDirectory()) {
				if(recursive) {
					serveAll(path+"/"+f.getName(),f.getAbsolutePath(),recursive,ignoreHiddenFiles);
				}
			} else {
				if(!f.isHidden() || !ignoreHiddenFiles) {
					serve(path+"/"+f.getName(),f.getAbsolutePath());
				}
			}
		}
	}

	
//	private List<File> listFilesRecursive(String folderName) {
//		List<File> fileList = new ArrayList<File>();
//		recurseDir(fileList, folderName);
//		return fileList;
//	}
//
//	void recurseDir(List<File> a, String folderName) {
//		File file = new File(folderName);
//		if (file.isDirectory()) {
//			// If you want to include directories in the list
//			a.add(file);
//			File[] subfiles = file.listFiles();
//			for (int i = 0; i < subfiles.length; i++) {
//				// Call this function on all files in this directory
//				recurseDir(a, subfiles[i].getAbsolutePath());
//			}
//		} else {
//			a.add(file);
//		}
//	}

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
			server.createContext("/" + path, fileHandler);
			logger.info("Serving: "+fileName +" ");
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
		server.createContext("/" + path, handler);
		if (logger.isLoggable(Level.CONFIG)) {
			if (handler.getClass() == SimpleFileHandler.class) {
				logger.config("Serving: " + ((SimpleFileHandler) handler).fileName + " on path: /" + path);
			} else if (handler.getClass().getSuperclass() == TemplateFileHandler.class) {
				logger.config("Serving template: " + ((TemplateFileHandler) handler).fileName + " on path: /" + path);
			} else if (handler.getClass().getSuperclass() == DynamicResponseHandler.class) {
				logger.config("Serving Dynamic response on path: /" + path);
			}
		}
	}

	public Method getCallbackMethod(String callbackFunctionName) {
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

	public void setIndexCallback(String callbackFunctionName) {
		Class<? extends PApplet> clazz = parent.getClass();
		try {
			Method method = clazz.getDeclaredMethod(callbackFunctionName,
					new Class<?>[] { String.class, HashMap.class });
			indexFileHandler.setCallbackMethod(method);
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("No such Method: " + callbackFunctionName + " in PApplet");
		}
	}

	public PApplet getParent() {
		return parent;
	}

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

	public void removeContext(String path) {
		try {
			server.removeContext("/" + path);
			logger.config("Removing context for path: /" + path);
		} catch (IllegalArgumentException iaExc) {
			logger.warning(iaExc.getMessage());
			logger.warning("Context at path: /" + path + " cannot be removed. Does the path exist?");
		}
	}

	public static void setLoggerLevel(Level level) {
		logger.setLevel(level);
	}
}
