package http.prefab.test;

import http.DynamicResponseHandler;
import http.SimpleHTTPServer;
import http.prefab.ClassGui;
import http.prefab.DatGui;
import processing.core.PApplet;

public class Tester extends PApplet {

	SimpleHTTPServer server;
	DatGui gui;
	TestClass tc,tc2;
	
	public int red,green,blue;

	public void settings() {
		size(600, 200);
	}

	public void setup() {
		server = new SimpleHTTPServer(this);
		gui = new DatGui(server);
		TestClass.parent = this;
		tc = new TestClass(50);
		ClassGui cg = gui.add(tc,new String[]{"level","speed","y"});
		cg.getValueElement("level").min(1).max(30);
		cg.getValueElement("speed").min(-20).max(20);
		cg.addMethodTrigger("reset");

		tc2 = new TestClass(120);
		cg = gui.add(tc2,new String[]{"level","speed","y"});
		cg.getValueElement("level").min(1).max(30);
		cg.getValueElement("speed").min(-20).max(20);		
		cg.isOpen = false;
		
		ClassGui cg2 = gui.add(this,new String[]{"red","green","blue"});
		cg2.getValueElement("red").min(0).max(255);
		cg2.getValueElement("green").min(0).max(255);
		cg2.getValueElement("blue").min(0).max(255);
		
		TestClass2 arrayObj = new TestClass2();
		ClassGui tc1 = gui.add(arrayObj,new String[]{"vals"});
		
		
		// gui.add(tc.getClass());
		gui.build();
		DynamicResponseHandler handler = gui.getHandler();
		server.createContext("datgui", handler);
	}


	public void draw() {
		background(red,green,blue);
		if (tc.red)
			fill(255, 0, 0);
		else
			fill(255);
		tc.draw();
		tc2.draw();
	}

	public static void main(String[] args) {
		PApplet.main("http.prefab.test.Tester");
	}
}
