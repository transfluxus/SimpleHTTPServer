package http.prefab;

import processing.data.JSONObject;

public class BangGuiElement extends GuiElement {

	final String clazzName;
	
	public BangGuiElement(String clazzName, String name) {
		super(name);
		this.clazzName = clazzName;
	}

	@Override
	public String preDefBuild() {
		//String nl = System.getProperty("line.separator");
		JSONObject clazzJSONObj = new JSONObject();
		JSONObject fctJSONObj = new JSONObject();
		fctJSONObj.setString("function", name);
		clazzJSONObj.setJSONObject(clazzName, fctJSONObj);
		return "this."+name+" = function() { send('"+clazzJSONObj.toString()+"'); };";
	}
	
	@Override
	public String build(String addTo,String objectName) {
		return addTo+".add("+objectName+", '"+name+"')";
	}

}
