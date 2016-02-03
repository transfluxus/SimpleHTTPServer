package http.prefab.guiElement;

import java.util.Optional;

public class StringListElement extends GuiElement {

	private final String values;
	private String defaultValue;
	
	public StringListElement(String name,String defaultValue, String values) {
		super(name);
		this.values = values;
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String preDefBuild() {
		return "this."+name+ " = " +defaultValue+";";
	}

	@Override
	public String build(String addTo, String objectName) {
		StringBuilder sb= new StringBuilder(addTo+".add("+objectName+", '"+name+"',"+values+")");
		String className = objectName.substring(0,objectName.length()-3);
		sb.append(".onChange(function(value) { addToUpdate('"+className+"','"+name+"',"+objectName+"."+name+"); });");
		return sb.toString();
	}

}
