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
	
	public int red,green,blue;

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
		ClassGui cg2 = gui.addToUpdate(this);
		cg2.getValueElement("red").min(0).max(255);
		cg2.getValueElement("green").min(0).max(255);
		cg2.getValueElement("blue").min(0).max(255);
		// gui.add(tc.getClass());
		gui.build();
		DynamicResponseHandler handler = gui.getHandler();
		server.createContext("datgui", handler);
	}

	float x = 0;

	public void draw() {
		background(red,green,blue);
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
