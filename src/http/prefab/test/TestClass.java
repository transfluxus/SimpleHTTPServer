package http.prefab.test;

import java.io.File;

import http.prefab.ClassGui;
import http.prefab.DatGui;

public class TestClass {

	public int level = 2;
	public float speed = 0.2f;
	public boolean red = false;
	
	
	public static void main(String[] args) {
		String templateFilePath = new File("").getAbsolutePath()+"/data/templates.txt";
		//System.out.println(templateFilePath);
		DatGui gui = new DatGui(templateFilePath);
		ClassGui cg = gui.add(TestClass.class);
		cg.getValueElement("speed").min(0).max(1);
		System.out.println(gui.build());
	}
	
	public void reset() {
		level = 2;
		speed = 0.2f;
	}
}
