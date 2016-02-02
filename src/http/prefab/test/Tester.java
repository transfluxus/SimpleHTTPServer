package http.prefab.test;

import http.DynamicResponseHandler;
import http.ResponseBuilder;
import http.SimpleHTTPServer;
import http.prefab.DatGui;
import processing.core.PApplet;
import processing.data.JSONObject;

public class Tester extends PApplet {

	SimpleHTTPServer server;
	DatGui gui;
	TestClass tc;

	public void settings() {

	}

	public void setup() {
		server = new SimpleHTTPServer(this);
		gui = new DatGui(server);
		tc = new TestClass();
		gui.add(tc.getClass());
		gui.build();
		
		DynamicResponseHandler responder = new DynamicResponseHandler(new JSONEcho(), "application/json");
		server.createContext("datgui", responder);
	}
	
	class JSONEcho extends ResponseBuilder {

		  /**
		  This abstract function needs to be overwritten. 
		  In this example the json request needs to include a requestNumber.
		  It then returns the same JSONObject but adds responseNumber,which is the double
		  of requestNumber
		  */
		  public  String getResponse(String requestBody) {
		    JSONObject json = parseJSONObject(requestBody);
		   // int number = json.getInt("requestNumber");
		   // json.setInt("responseNumber", number*2);
		    System.out.println(json);
		    return json.toString();
		  }
		}

	public void draw() {

	}

	public static void main(String[] args) {
		PApplet.main("http.prefab.test.Tester");
	}
}
