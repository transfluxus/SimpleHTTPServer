package http.prefab;

public class BoolGuiElement extends GuiElement {

	public BoolGuiElement(String name) {
		super(name, "false");
	}

	public BoolGuiElement(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public String preDefBuild() {
		return "this."+name+ " = " +defaultValue+";";
	}

	@Override
	public String build(String addTo, String objectName) {
		StringBuilder sb= new StringBuilder(addTo+".add("+objectName+", '"+name+"')");
		String className = objectName.substring(0,objectName.length()-3);
		sb.append(".onChange(function(value) { addToUpdate('"+className+"','"+name+"',"+objectName+"."+name+"); });");
		return sb.toString();
	}

}
