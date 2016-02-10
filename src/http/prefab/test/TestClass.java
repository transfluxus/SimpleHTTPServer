package http.prefab.test;

import java.io.File;

import http.prefab.ClassGui;
import http.prefab.DatGui;
import processing.core.PApplet;

public class TestClass {

	public static PApplet parent;
	
	public int level = 2;
	public float speed = 0.2f;
	public boolean red = false;
	public float y;
	public float x;
	
	public TestClass(int y) {
		this.y = y;
	}
	
	public void reset() {
		level = 2;
		speed = 0.2f;
	}


	public void update() {
		x = (x + parent.width + speed) % parent.width;		
	}


	public void draw() {
		parent.ellipse(x,y, level, level);
		update();
	}
}
