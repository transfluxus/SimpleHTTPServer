package http.prefab;

public abstract class GuiElement {

	public final String name;
		
	String defaultValue;
	
	boolean autoSend = true;
	
	public GuiElement(String name) {
		this(name,"0");
	}
	
	public GuiElement(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	abstract public String preDefBuild();

	abstract public String build(String addTo,String objectName);
}

