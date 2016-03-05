package http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class TemplateFileHandler extends SimpleFileHandler {

	private Template template;
	private Map<String, Object> root = new HashMap<>();

	public TemplateFileHandler(String templateFileName) {
		super(templateFileName);
		try {
			SimpleHTTPServer.setupFMConfig(parent.sketchPath()+"/data/");
			template = SimpleHTTPServer.freeMarker_configuration.getTemplate(templateFileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("TemplateFileHandler: Template not readable: " + templateFileName);
		}
	}

	// public void addModel(String key, String value) {
	// root.put(key, value);
	// }

	abstract public void createMap();
	
	/**
	 *
	 * @param key
	 * @param value
	 *            any of String,Number,Boolean,List,Map
	 */
	public void addModel(String key, Object value) {
		root.put(key, value);
	}

	@Override
	protected byte[] getResponseBytes() {
		logger.config("Building page from template: "+fileName);
		createMap();
		byte[] bytearray = new byte[0];
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			template.process(root, new OutputStreamWriter(bos));
			bytearray = bos.toByteArray();
		} catch (IOException ioExc) {
			System.err.println("error reading file: " + fileName);
		} catch (TemplateException tempExc) {
			System.err.println("Error creating file from template: " + fileName);
		}
		return bytearray;
	}
}
