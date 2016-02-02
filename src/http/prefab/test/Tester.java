package http.prefab.test;

import http.SimpleHTTPServer;
import http.prefab.DatGui;
import processing.core.PApplet;

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
		System.out.println(gui.build());
	}
	
	public void draw() {
		
	}
	
	public static void main(String[] args) {
		PApplet.main("http.prefab.test.Tester");
	}
}
