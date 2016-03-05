package http.prefab.test;

import java.util.logging.Level;

import http.SimpleHTTPServer;
import http.prefab.ClassGui;
import http.prefab.DatGui;
import processing.core.PApplet;

public class Tester extends PApplet {

	SimpleHTTPServer server;
	DatGui gui;
	MovingCircle tc, tc2;
	TestClass2 arrayObj;

	public int red, green;
	public int blue = 100;

	@Override
	public void settings() {
		size(600, 200);
	}

	@Override
	public void setup() {
		server = new SimpleHTTPServer(this);
		gui = new DatGui(server);
		MovingCircle.parent = this;
		tc = new MovingCircle(50);
		ClassGui cg = gui.add(tc, new String[] { "level", "speed", "y","red"});
		cg.getValueElement("level").min(1).max(30);
		cg.getValueElement("speed").min(-20).max(20);
		cg.setBoundary("y",0,height);
		cg.addMethodTrigger("reset");

		tc2 = new MovingCircle(120);
		cg = gui.add(tc2, new String[] { "level", "speed", "y" });
		cg.getValueElement("level").min(1).max(30);
		cg.getValueElement("speed").min(-20).max(20);
		cg.setBoundary("y",0,height);
		cg.isOpen = false;

		ClassGui cg2 = gui.add(this, new String[] { "red", "green", "blue" });
		// addSelector(this,new String[]{"red","green","blue"});
		cg2.getValueElement("red").min(0).max(255);
		cg2.getValueElement("green").min(0).max(255);
		cg2.getValueElement("blue").min(0).max(255);

		arrayObj = new TestClass2();
		ClassGui tc1 = gui.add(arrayObj);
		tc1.addSelector("vals", 1, "value");

		gui.setLogLevel(Level.SEVERE);
		// gui.add(tc.getClass());
		gui.build("bla.js");
	}

	@Override
	public void draw() {
		background(red, green, blue);
		switch (arrayObj.value) {
		case "Stroke":
			stroke(255);
			noFill();
			break;
		case "Fill":
			noStroke();
			if (tc.red)
				fill(255, 0, 0);
			else
				fill(255);
			break;
		case "StrokeFill":
			stroke(255);
			if (tc.red)
				fill(255, 0, 0);
			else
				fill(255);
			break;
		}

		tc.draw();
		tc2.draw();
	}

	public static void main(String[] args) {
		PApplet.main("http.prefab.test.Tester");
	}
}
