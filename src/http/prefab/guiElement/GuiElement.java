package http.prefab.guiElement;

import java.lang.reflect.Field;
import java.util.Optional;

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
	
	abstract public Optional<GuiElement> FromField(Field field,Object obj);

	abstract public String preDefBuild();

	abstract public String build(String addTo,String objectName);
}

