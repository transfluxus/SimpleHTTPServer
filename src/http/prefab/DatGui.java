package http.prefab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.Handler;
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
	final String templateFileLocation;

	List<ClassGui> classGuis = new ArrayList<>();
	int sendDelay = 100;

	private AutoUpdateContext updateContext;

	String nl = System.getProperty("line.separator");
	StringBuilder builder;
	Optional<FileWriter> fw;

	protected static Logger logger = Logger.getLogger("	");

	public DatGui(SimpleHTTPServer server) {
		this.server = server;
		this.templateFileLocation = server.getParent().sketchPath() + "/data/templates.txt";
		updateContext = new AutoUpdateContext(server.getParent());
		logger.getParent().getHandlers()[0].setFormatter(new DatGUILogFormatter());
	}

	public ClassGui add(Object relatedObject) {
		return add(relatedObject, new String[] {});
	}

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
			return new BufferedReader(new FileReader(this.templateFileLocation));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find html template file: " + this.templateFileLocation);
			System.exit(1);
			return null;
		}
	}

	public String build() {
		return build("index.html");
	}

	public String build(String fileName) {
		// boolean completeHTML = server.isPresent();
		this.builder = new StringBuilder();

		FileWriter fww = null;
		String filePath = server.getParent().sketchPath() + "/data/" + fileName;
		try {
			fww = new FileWriter(filePath);
			fw = Optional.of(fww);
		} catch (IOException e) {
			System.err.println("Cannot write to file: ");
			System.err.println(filePath);
			fw = Optional.empty();
		}

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
			/*
			 * while (!templateString.startsWith("### html-end")) {
			 * builder.append(templateString + nl); }
			 */
			// classObjects
			classGuis.stream().forEach(cl -> {
				String classFunc = cl.buildFunction();
				appendToOutput(classFunc);
			});

			appendToOutput("window.onload = function() {");
			classGuis.stream().forEach(cl -> appendToOutput(cl.build()));
			appendToOutput("};");
			appendToOutput("setInterval(autoSend," + sendDelay + ");");
			appendToOutput("</script>");

			while ((templateString = templateReader.readLine()) != null) {
				appendToOutput(templateString);
			}
			if (fw.isPresent())
				fw.get().close();

			if (server.isRunning()) {
				if (!fileName.equals("index.html")) {
					server.serve(fileName);
				}
				server.serve("dat.gui.js");
			} else {
				logger.warning("Server is not running, hence not gonna serve any files");
			}
		} catch (IOException e) {
			System.err.println("Error reading template file");
			System.exit(1);
		}

		// adding std-handler to
		if (server.isRunning()) {
			DynamicResponseHandler handler = getHandler();
			server.createContext("datgui", handler);
		}
		return builder.toString();
	}

	public void appendToOutput(String text) {
		builder.append(text + nl);
		if (fw.isPresent()) {
			try {
				fw.get().append(text + nl);
			} catch (IOException e) {
				System.err.println("Cannot write to file");
			}
		}
	}

	public void setSendDelay(int millis) {
		sendDelay = millis;
	}

	public AutoUpdateContext getUpdateContext() {
		return updateContext;
	}

	public DynamicResponseHandler getHandler() {
		return new DynamicResponseHandler(getUpdateContext(), "application/json");
	}

	public void addToHeader(String headerText) {
		// TODO
	}

	public void addToBody(String bodyText) {
		// TODO
	}

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
