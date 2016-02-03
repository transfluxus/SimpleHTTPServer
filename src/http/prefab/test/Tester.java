package http.prefab.test;

import http.DynamicResponseHandler;
import http.ResponseBuilder;
import http.SimpleHTTPServer;
import http.prefab.ClassGui;
import http.prefab.DatGui;
import processing.core.PApplet;
import processing.data.JSONObject;

public class Tester extends PApplet {

	SimpleHTTPServer server;
	DatGui gui;
	TestClass tc;

	public void settings() {
		size(600, 200);
	}

	public void setup() {
		server = new SimpleHTTPServer(this);
		gui = new DatGui(server);
		tc = new TestClass();
		ClassGui cg = gui.addToUpdate(tc);
		cg.getValueElement("level").min(1).max(30);
		cg.getValueElement("speed").min(-20).max(20);
		cg.addMethodTrigger("reset");

		// gui.add(tc.getClass());
		gui.build();
		DynamicResponseHandler responder = new DynamicResponseHandler(gui.getUpdateContext(), "application/json");
		server.createContext("datgui", responder);

		// server.createContext("datgui", responder);
	}

	class JSONEcho extends ResponseBuilder {

		/**
		 * This abstract function needs to be overwritten. In this example the
		 * json request needs to include a requestNumber. It then returns the
		 * same JSONObject but adds responseNumber,which is the double of
		 * requestNumber
		 */
		public String getResponse(String requestBody) {
			JSONObject json = parseJSONObject(requestBody);
			// int number = json.getInt("requestNumber");
			// json.setInt("responseNumber", number*2);
			System.out.println(json);
			return json.toString();
		}
	}

	float x = 0;

	public void draw() {
		background(0);
		if (tc.red)
			fill(255, 0, 0);
		else
			fill(255);
		ellipse(x, height / 2, tc.level, tc.level);
		x = (x + width + tc.speed) % width;
	}

	public static void main(String[] args) {
		PApplet.main("http.prefab.test.Tester");
	}
}
