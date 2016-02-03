package http.prefab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import http.DynamicResponseHandler;
import http.SimpleHTTPServer;

public class DatGui {

	final Optional<SimpleHTTPServer> server;
	final String templateFileLocation;

	List<ClassGui> classGuis = new ArrayList<>();
	int sendDelay = 100;
	
	private AutoUpdateContext updateContext;

	String nl = System.getProperty("line.separator");
	StringBuilder builder;
	Optional<FileWriter> fw;

	public DatGui(SimpleHTTPServer server) {
		this.server = Optional.of(server);
		this.templateFileLocation = server.getParent().sketchPath() + "/data/templates.txt";
		updateContext = new AutoUpdateContext(server.getParent());
	}

	public DatGui(String templateFileLocation) {
		server = Optional.empty();
		this.templateFileLocation = templateFileLocation;
	}

	public ClassGui add(Class<?> clazz) {
		ClassGui cg = new ClassGui(clazz);
		classGuis.add(cg);
		return cg;
	}
	
	public ClassGui addToUpdate(Object obj) {
		ClassGui cg = add(obj.getClass());
		updateContext.add(obj,cg);
		return cg;	
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
		boolean completeHTML = server.isPresent();
		this.builder = new StringBuilder();

		if (completeHTML) {
			FileWriter fww = null;
			try {
				fww = new FileWriter(server.get().getParent().sketchPath() + "/data/" + fileName);
			} catch (IOException e) {
				System.err.println("Cannot write to file: ");
				System.err.println(server.get().getParent().sketchPath() + "/data/" + fileName);
			}
			if (fww == null) {
				fw = Optional.empty();
			} else {
				fw = Optional.of(fww);
			}
		} else {
			fw = Optional.empty();
		}

		BufferedReader templateReader = templateFileReader();
		// String dNl = nl + nl;
		// scripts

		String templateString;
		try {
			boolean addScriptLines = false;
			while (true) {
				templateString = templateReader.readLine();
				// System.out.println(templateString);
				if (templateString.startsWith("### html-end"))
					break;
				if (templateString.startsWith("### script")) {
					addScriptLines = true;
					continue;
				}
				if (completeHTML || addScriptLines) {
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
			appendToOutput("setInterval(autoSend,"+sendDelay+");");
			appendToOutput("</script>");

			if (completeHTML) {
				while ((templateString = templateReader.readLine()) != null) {
					appendToOutput(templateString);
				}
			}
			if(fw.isPresent())
				fw.get().close();
			
			if(completeHTML) {
				if(!fileName.equals("index.html"));
					server.get().serve(fileName);
				server.get().serve("dat.gui.js");
			}
		} catch (IOException e) {
			System.err.println("Error reading template file");
			System.exit(1);
		}
		return builder.toString();
	}

	public void appendToOutput(String text) {
		builder.append(text + nl);
		if (fw.isPresent())
			try {
				fw.get().append(text + nl);
			} catch (IOException e) {
				System.err.println("Cannot write to file");
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
}
