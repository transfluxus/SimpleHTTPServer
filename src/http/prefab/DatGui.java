package http.prefab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
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
	final URL templateFileLocation;

	List<ClassGui> classGuis = new ArrayList<>();
	int sendDelay = 100;

	private AutoUpdateContext updateContext;

	String nl = System.getProperty("line.separator");
	StringBuilder builder;
	Optional<FileWriter> fw;

	protected static Logger logger = Logger.getLogger("	");

	public DatGui(SimpleHTTPServer server) {
		this.server = server;
		this.templateFileLocation = DatGui.class.getClassLoader().getResource("http/prefab/templates.txt");
		updateContext = new AutoUpdateContext(server.getParent());
		logger.getParent().getHandlers()[0].setFormatter(new DatGUILogFormatter());
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

	BufferedReader templateFileReader() {
		try {
			return new BufferedReader(new InputStreamReader(templateFileLocation.openStream()));
		} catch ( IOException e) {
			System.err.println("Cannot find html template file: " + this.templateFileLocation);
			System.exit(1);
			return null;
		}
	}

	public String build() {
		return build("index.html");
	}
	
	public void copyDatGuiJS() {
		File datgui_Destination = new File(server.getParent().sketchPath()+"/dat.gui.js");
	/*	if(!datgui_Destination.exists()) {
			System.out.println(datgui_Destination.getAbsolutePath() + " exists");
			return;
		}*/
		System.out.println("doit");
		System.out.println("dd");
		try {
			InputStream is =  DatGui.class.getClassLoader().getResourceAsStream("http/prefab/dgjs");
			FileOutputStream fos = new FileOutputStream(datgui_Destination);
			for(int r; (r = is.read()) != -1;)
				fos.write(r);
			is.close();
			fos.close();
			logger.info("Creating dat.gui.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String build(String fileName) {
		// boolean completeHTML = server.isPresent();
		this.builder = new StringBuilder();
		FileWriter fww = null;
		String filePath = server.getParent().sketchPath() +"/"+ fileName;
		
		try {
			fww = new FileWriter(filePath);
			fw = Optional.of(fww);
		} catch (IOException e) {
			System.err.println("Cannot write to file: ");
			System.out.println(e.getMessage());
			System.err.println(filePath);
			fw = Optional.empty();
		}
		System.out.println("B1");
		BufferedReader templateReader = templateFileReader();
		// String dNl = nl + nl;
		// scripts

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
			System.out.println("B2");
			/*
			 * while (!templateString.startsWith("### html-end")) {
			 * builder.append(templateString + nl); }
			 */
			// classObjects
			classGuis.stream().forEach(cl -> {
				String classFunc = cl.buildFunction();
				appendToOutput(classFunc);
			});
			System.out.println("B3");
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
				System.out.println("jo");
				server.serve(server.getParent().sketchPath()+"/dat.gui.js");
			} else {
				logger.warning("Server is not running, hence not gonna serve any files");
			}
		} catch (IOException e) {
			System.err.println("Error reading template file");
			System.exit(1);
		}
		System.out.println("B4");
		// adding std-handler to
		if (server.isRunning()) {
			DynamicResponseHandler handler = getHandler();
			server.createContext("datgui", handler);
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
