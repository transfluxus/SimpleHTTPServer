package examples.responseFromParameters;

import java.util.Map;

import http.SimpleHTTPServer;
import http.TemplateFileHandler;
import processing.core.PApplet;

public class ResponseFromParameters extends PApplet {

	SimpleHTTPServer server;

	@Override
	public void settings() {

	}

	@Override
	public void setup() {
		// Create a server listening on port 8000
		server = new SimpleHTTPServer(this);
		server.serve(sketchPath() + "/data/examples/responseFromParameters/", "index.html");
		TemplateFileHandler responder = new ResultFromPost("examples/responseFromParameters/params.html");
		server.createContext("params.html", responder);
	}

	public class ResultFromPost extends TemplateFileHandler {

		public ResultFromPost(String templateFileName) {
			super(templateFileName);
		}

		@Override
		public void createMap() {
			Map<String,String> params = queryToMap();
			float result = 0;
			if(params.containsKey("a") && params.containsKey("b")) {
				result = Float.valueOf(params.get("a")) * Float.valueOf(params.get("b"));
			}
			addModel("result", ""+result);
		}
	}

	public static void main(String[] args) {
		PApplet.main("examples.responseFromParameters.ResponseFromParameters");
	}

}
