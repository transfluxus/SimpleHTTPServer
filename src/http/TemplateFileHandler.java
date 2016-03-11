package http;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class TemplateFileHandler extends FileHandler {

	private Template template;
	private Map<String, Object> root = new HashMap<>();

	public TemplateFileHandler(String templateFileName) {
		super();
		this.fileName = templateFileName;
		try {
			this.file = getFile(templateFileName);
		} catch (FileNotFoundException e1) {
			SimpleHTTPServer.logger.warning("Templatefile not found. That's not gonna work");
		}
		try {
			SimpleHTTPServer.setupFMConfig(parent.sketchPath() + "/data/");
			template = SimpleHTTPServer.freeMarker_configuration.getTemplate(templateFileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("TemplateFileHandler: Template not readable: " + templateFileName);
		}
	}


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

	protected byte[] getResponseBytes() {
		logger.config("Building page from template: " + fileName);
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
