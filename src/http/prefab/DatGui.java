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

	final SimpleHTTPServer server;
	BufferedReader templateReader;
	BufferedReader datGuiReader;

	List<ClassGui> classGuis = new ArrayList<>();
	int sendDelay = 100;

	private AutoUpdateContext updateContext;

	String nl = System.getProperty("line.separator");
	StringBuilder builder;
	Optional<FileWriter> fw;

	protected static Logger logger = Logger.getLogger("	");

	public DatGui(SimpleHTTPServer server) {
		this.server = server;
		templateReader = getReader("data/datgui_html_template.txt");
		datGuiReader = getReader("data/datgui.js");
		updateContext = new AutoUpdateContext(server.getParent());
		logger.getParent().getHandlers()[0].setFormatter(new DatGUILogFormatter());
	}

	BufferedReader getReader(String file) {
		BufferedReader reader = null;
		InputStream s = DatGui.class.getClassLoader().getResourceAsStream(file);
		if(s == null) {
			try {
				reader = new BufferedReader(new FileReader(new File(file)));
			} catch (FileNotFoundException e) {
				System.err.println("Template file not found...");
				System.exit(1);
			}
		} else {
			reader  = new BufferedReader(new InputStreamReader(s));
		}
		return reader;
	}

	/**
	 * Add an Object to the gui, which you want to edit in the gui
	 * @param relatedObject
	 * @return
	 */
	public ClassGui add(Object relatedObject) {
		return add(relatedObject, new String[] {});
	}

	/**
	 * Add an Object to the gui, with the specified public fields
	 * 
	 * @param relatedObject
	 * @param fieldNames Array of Fieldnames
	 * @return
	 */
	public ClassGui add(Object relatedObject, String[] fieldNames) {
		Class<?> clazz = relatedObject.getClass();
		int id = getId(clazz);
		ClassGui cg = new ClassGui(clazz, relatedObject, id);
		for (String fieldName : fieldNames) {
			cg.addFieldGui(fieldName);
		}
		classGuis.add(cg);
		updateContext.add(relatedObject, cg);
		return cg;
	}

	public void allPublics() {
		classGuis.stream().forEach(ClassGui::allPublics);
	}

	private int getId(Class<?> clazz) {
		int id = (int) classGuis.stream().filter(cg -> cg.getClazz().equals(clazz)).count() + 1;
		return id;
	}


	public String build() {
		return build("data/index.html");
	}
	
	public void copyDatGuiJS() {
		File datgui_Destination = new File(server.getParent().sketchPath()+"/data/dat.gui.js");
		try {
			FileOutputStream fos = new FileOutputStream(datgui_Destination);
			for(int r; (r = datGuiReader.read()) != -1;){
				fos.write(r);
			}
			datGuiReader.close();
			fos.close();
			logger.info("Creating dat.gui.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String build(String fileName) {
		this.builder = new StringBuilder();
		FileWriter fww = null;
		String filePath = server.getParent().sketchPath() +"/data/"+ fileName;
		
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

			if(htmlFile) {
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
				server.serve(server.getParent().sketchPath()+"/data/dat.gui.js");
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
			server.serve("",fileName);
			server.serve("dat.gui.js");
		}
		return builder.toString();
	}


	
	public ClassGui getClassGui(Object relObj) {
		// J7
		Optional<ClassGui> cg = Optional.empty();
		for(ClassGui c : classGuis) {
			if(c.getRelatedObject() == relObj) 
				cg = Optional.of(c);
		}
		// J8
		//Optional<ClassGui> cg = classGuis.stream().filter(c -> c.getRelatedObject() == relObj).findAny();
		
		if(!cg.isPresent()) {
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
	 * @param millis
	 */
	public void setSendDelay(int millis) {
		sendDelay = millis;
	}

	private DynamicResponseHandler getHandler() {
		return new DynamicResponseHandler(updateContext, "application/json");
	}
	
	/*
	public AutoUpdateContext getUpdateContext() {
		return updateContext;
	}

	public void addToHeader(String headerText) {
		// TODO
	}

	public void addToBody(String bodyText) {
		// TODO
	}
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
