package http.prefab;

public class StringListElement extends GuiElement {

	private final String values, targetValueName;
	private String defaultValue;
	
	public StringListElement(String name,String defaultValue, String values, String targetValueName) {
		super(name);
		this.values = values;
		this.defaultValue = defaultValue;
		this.targetValueName = targetValueName;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String preDefBuild() {
		return "this."+targetValueName+ " = " +defaultValue+";";
	}

	@Override
	public String build(String addTo, String objectName) {
		StringBuilder sb= new StringBuilder(addTo+".add("+objectName+",'"+targetValueName+"',"+values+")");
		String className = objectName.substring(0,objectName.length()-3);
		sb.append(".onChange(function(value) { addToUpdate('"+className+"','"+targetValueName+"',"+objectName+"."+targetValueName+"); });");
		return sb.toString();
	}

}
