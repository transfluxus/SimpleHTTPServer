package http.prefab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import http.DynamicResponseHandler;
import http.SimpleHTTPServer;

/*
 * addToHeader
 * addToBody
 * write to js file (test)
 * have multiple guy windows
 * callbacks. valuechange trigger fct calls in processing
 * new build, instead of stops, replace templates
 */
public class DatGui {

	private final SimpleHTTPServer server;
	private BufferedReader templateReader;
	private BufferedReader datGuiReader;

	private List<ClassGui> classGuis = new ArrayList<>();
	private int sendDelay = 100;

	private AutoUpdateContext updateContext;

	private String nl = System.getProperty("line.separator");
	private StringBuilder builder;
	private Optional<FileWriter> fw;
	private boolean manualUpdate;

	protected static Logger logger = Logger.getLogger("datguiLogger");

	/**
	 * Datgui constructor requires a SimpleHTTPServer
	 * 
	 * @param server
	 */
	public DatGui(SimpleHTTPServer server) {
		this.server = server;
		templateReader = getReader("data/datgui_html_template.txt");
		datGuiReader = getReader("data/datgui.js");
		updateContext = new AutoUpdateContext(server.getParent());
		logger.getParent().getHandlers()[0].setFormatter(new DatGUILogFormatter());
	}

	/**
	 * Reader for the templatefile and dat.gui.js library
	 * 
	 * @param file
	 *            file to read from
	 * @return Bufferedreader to read line by line
	 */
	private BufferedReader getReader(String file) {
		BufferedReader reader = null;
		InputStream s = DatGui.class.getClassLoader().getResourceAsStream(file);

		// DatGui.class.getClassLoader()
		if (s == null) {
			try {
				reader = new BufferedReader(new FileReader(new File(file)));
			} catch (FileNotFoundException e) {
				System.err.println("Template file not found...");
				System.exit(1);
			}
		} else {
			reader = new BufferedReader(new InputStreamReader(s));
		}
		return reader;
	}

	/**
	 * Add an Object to the gui, which you want to edit in the gui
	 * 
	 * @param relatedObject
	 * @return returns the ClassGui for that Object
	 */
	public ClassGui add(Object relatedObject) {
		return add(relatedObject, new String[] {});
	}

	/**
	 * Add an Object to the gui, with the specified public fields
	 * 
	 * @param relatedObject the object that should be updated
	 * @param fieldNames
	 *            Array of Fieldnames of public fields
	 * @return returns the ClassGui for that Object
	 */
	public ClassGui add(Object relatedObject, String[] fieldNames) {
		ClassGui cg = createClassGui(relatedObject);
		for (String fieldName : fieldNames) {
			cg.addFieldGui(fieldName);
		}
		registerClassGui(cg);
		return cg;
	}

	private void registerClassGui(ClassGui cg) {
		if (!classGuis.contains(cg)) {
			classGuis.add(cg);
			updateContext.add(cg.getRelatedObject(), cg);
		}
	}

	/**
	 * Add a specific field to the gui and sets its minimum value. that field
	 * must be declared public and must have the type boolean,int or float
	 * 
	 * @param relatedObject the object 
	 * @param fieldName
	 * @return
	 */
	public ClassGui add(Object relatedObject, String fieldName) {
		ClassGui cg = createClassGui(relatedObject);
		cg.addFieldGui(fieldName);
		registerClassGui(cg);
		return cg;
	}
	
	/**
	 * Add a specific field to the gui and sets its minimum value. that field
	 * must be declared public and must have the type int or float.
	 * 
	 * @param relatedObject 
	 * @param fieldName
	 * @param min
	 */
	public ClassGui add(Object relatedObject, String fieldName, float min) {
		ClassGui cg = createClassGui(relatedObject);
		cg.addFieldGui(fieldName, min);
		registerClassGui(cg);
		return cg;
	}

	public ClassGui add(Object relatedObject, String fieldName, float min, float max) {
		ClassGui cg = createClassGui(relatedObject);
		cg.addFieldGui(fieldName, min, max);
		registerClassGui(cg);
		return cg;
	}

	public ClassGui addSelector(Object relatedObject, String arrayName, int defaultIndex, String targetFieldName) {
		ClassGui cg = createClassGui(relatedObject);
		cg.addSelector(arrayName, defaultIndex, targetFieldName);
		registerClassGui(cg);
		return cg;
	}

	public ClassGui addMethodTrigger(Object relatedObject, String methodName) {
		ClassGui cg = createClassGui(relatedObject);
		cg.addMethodTrigger(methodName);
		registerClassGui(cg);
		return cg;
	}

	private ClassGui createClassGui(Object relatedObject) {
		ClassGui cg = null;
		try {
			cg = getClassGui(relatedObject);
		} catch (NullPointerException notThere) {
			Class<?> clazz = relatedObject.getClass();
			int id = getId(clazz);
			cg = new ClassGui(clazz, relatedObject, id);
		}
		return cg;
	}

	/**
	 * Add all public fields of all Classguis added so far
	 */
	public void allPublics() {
		classGuis.stream().forEach(ClassGui::allPublics);
	}

	private int getId(Class<?> clazz) {
		int id = (int) classGuis.stream().filter(cg -> cg.getClazz().equals(clazz)).count() + 1;
		return id;
	}

	/**
	 * Standard build method creates a index.html in the data folder and serves
	 * it
	 */
	public void build() {
		build("index.html");
	}

	/**
	 * Java7 method to copy a file... TODO use Files.copy(Path source, Path
	 * target, CopyOption... options)
	 */
	private void copyDatGuiJS() {
		File datgui_Destination = new File(server.getParent().sketchPath() + "/data/dat.gui.js");
		// leave it for now, since it needs a separate method to get the stream
		// only(intead of the reader
		// Files.copy(datGuiReader, datgui_Destination);
		try {
			FileOutputStream fos = new FileOutputStream(datgui_Destination);
			for (int r; (r = datGuiReader.read()) != -1;) {
				fos.write(r);
			}
			datGuiReader.close();
			fos.close();
			logger.info("Creating dat.gui.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build Gui to a file: tested with html- TODO test js files
	 * 
	 * @param fileName
	 *            file to write to
	 */
	public void build(String fileName) {
		this.builder = new StringBuilder();
		FileWriter fww = null;
		String filePath = server.getParent().sketchPath() + "/data/" + fileName;

		try {
			fww = new FileWriter(filePath);
			fw = Optional.of(fww);
		} catch (IOException e) {
			System.err.println("Cannot write to file: ");
			System.out.println(e.getMessage());
			System.err.println(filePath);
			fw = Optional.empty();
		}

		boolean htmlFile = fileName.endsWith("html");
		String templateString;
		try {
			boolean addScriptLines = htmlFile;
			while (true) {
				templateString = templateReader.readLine();
				if (templateString.startsWith("### html-end"))
					break;
				if (templateString.startsWith("### script")) {
					addScriptLines = true;
					continue;
				}
				if (addScriptLines) {
					appendToOutput(templateString);
				}
			}

			classGuis.stream().forEach(cl -> {
				String classFunc = cl.buildFunction();
				appendToOutput(classFunc);
			});
			appendToOutput("window.onload = function() {");
			classGuis.stream().forEach(cl -> appendToOutput(cl.build()));
			appendToOutput("};");
			appendToOutput("setInterval(autoSend," + sendDelay + ");");

			if (htmlFile) {
				while ((templateString = templateReader.readLine()) != null) {
					appendToOutput(templateString);
				}
			}
			if (fw.isPresent())
				fw.get().close();
			if (server.isRunning()) {
				if (!fileName.equals("index.html")) {
					server.removeContext("");
					server.serve(fileName);
				}
				copyDatGuiJS();
				server.serve(server.getParent().sketchPath() + "/data/dat.gui.js");
			} else {
				logger.warning("Server is not running, hence not gonna serve any files");
			}
		} catch (IOException e) {
			System.err.println("Error reading template file");
			System.exit(1);
		}
		if (server.isRunning()) {
			DynamicResponseHandler handler = getHandler();
			server.createContext("datgui", handler);
			server.removeContext("");
			server.serve("", fileName);
			server.serve("dat.gui.js");
		}
		// return builder.toString();
	}

	/**
	 * Get the Classgui for an related Object, that has been added to DatGui
	 * 
	 * @param relObj
	 *            the related Object
	 * @return the Classgui for that object if present
	 */
	public ClassGui getClassGui(Object relObj) {
		// J7
		Optional<ClassGui> cg = Optional.empty();
		for (ClassGui c : classGuis) {
			if (c.getRelatedObject() == relObj)
				cg = Optional.of(c);
		}
		// J8
		// Optional<ClassGui> cg = classGuis.stream().filter(c ->
		// c.getRelatedObject() == relObj).findAny();

		if (!cg.isPresent()) {
			throw new NullPointerException("Requested Object is not added to the DatGui");
		} else {
			return cg.get();
		}
	}

	private void appendToOutput(String text) {
		builder.append(text + nl);
		if (fw.isPresent()) {
			try {
				fw.get().append(text + nl);
			} catch (IOException e) {
				System.err.println("Cannot write to file");
			}
		}
	}

	/**
	 * Sets the delay
	 * 
	 * @param millis
	 */
	public void setSendDelay(int millis) {
		sendDelay = millis;
	}
	
	/**
	 * If manualUpdate is set true gui changes are not send to the server automatically (after the set delay)
	 * but add an update button to update.
	 * 
	 * @param manualUpdate
	 */
	public void setManualUpdate(boolean manualUpdate) {
		this.manualUpdate = manualUpdate;
	}

	private DynamicResponseHandler getHandler() {
		return new DynamicResponseHandler(updateContext, "application/json");
	}

	/*
	 * public AutoUpdateContext getUpdateContext() { return updateContext; }
	 * 
	 * public void addToHeader(String headerText) { // TODO }
	 * 
	 * public void addToBody(String bodyText) { // TODO }
	 */

	private final class DatGUILogFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {
			return record.getLevel() + " : " + record.getMessage() + nl;
		}

	}

	public void setLogLevel(Level level) {
		logger.setLevel(level);
	}
}
