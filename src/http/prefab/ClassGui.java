package http.prefab;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import http.prefab.guiElement.BangGuiElement;
import http.prefab.guiElement.BoolGuiElement;
import http.prefab.guiElement.GuiElement;
import http.prefab.guiElement.StringListElement;
import http.prefab.guiElement.ValueGuiElement;
import http.prefab.guiElement.ValueGuiElement.TYPE;

public class ClassGui {

	private final Class<?> clazz;

	private String name;

	public boolean isOpen = true;

	private List<GuiElement> guiElements = new ArrayList<>();

	static String nl = System.getProperty("line.separator");

	private final Optional<Object> relatedObject;

	public ClassGui(Class<?> clazz, Object obj, int id) {
		this.clazz = clazz;
		this.name = clazz.getSimpleName() + id;
		//System.out.println("CG: "+name);
		if (obj != null)
			relatedObject = Optional.of(obj);
		else
			relatedObject = Optional.empty();
		allPublics();
	}

	public void allPublics() {
		Field[] fields = clazz.getDeclaredFields();
		Optional<Object> tempObj = Optional.empty();
		if (relatedObject.isPresent())
			tempObj = relatedObject;
		else {
			try {
				tempObj = Optional.of(clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println(
						"The class: " + name + " should provide a public default constructor for the default values");
			}
		}
		for (Field field : fields) {
			if (!Modifier.isPublic(field.getModifiers()))
				continue;
			String name = field.getName();
			Optional<? extends GuiElement> element = Optional.empty();
			//System.out.println(field);
			try {
				Class<?> type = field.getType();
				if (type.equals(Integer.TYPE)) {
					String defaultValue = "0";
					if(tempObj.isPresent())
						defaultValue = String.valueOf(field.getInt(tempObj.get()));
					element = Optional.of(new ValueGuiElement(name, TYPE.INT, defaultValue).step(1));
				} else if (type.equals(Float.TYPE)) {
					String defaultValue = "0.1";
					if(tempObj.isPresent())
						defaultValue = String.valueOf(field.getFloat(tempObj.get()));
					element = Optional.of(new ValueGuiElement(name, TYPE.FLOAT, defaultValue));
				} else if (type.equals(Boolean.TYPE)) {
					String defaultValue = "false";
					if(tempObj.isPresent())
						defaultValue = String.valueOf(field.getBoolean(tempObj.get()));
					element = Optional.of(new BoolGuiElement(name, defaultValue));
				} else if(type.isArray() && (
						//type.getComponentType().isPrimitive() || 
						type.getComponentType().equals(String.class))) {
					if(tempObj.isPresent()) {
						String[] vals = (String[])field.get(tempObj.get());
						for(int i=0; i < vals.length;i++) {
							vals[i] = "'"+vals[i]+"'";
						}
						if(vals.length > 0 && vals[0] != null) {
							String values = Arrays.toString(vals);
							element  = Optional.of(new StringListElement(name,vals[0],values));
						}
					}
				} else {
//					System.out.println(field.getType());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (element.isPresent()) {
				guiElements.add(element.get());
			}
		}
	}

	public void addMethodTrigger(String functionName) {
		try {
			clazz.getMethod(functionName, new Class<?>[] {});
			guiElements.add(new BangGuiElement(name, functionName));
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("Cannot find or acces method: " + functionName + " in class: " + name);
		}
	}

	public void remove(String fieldName) {
		for (Iterator<GuiElement> iter = guiElements.iterator(); iter.hasNext();) {
			if (iter.next().name.equals(fieldName)) {
				iter.remove();
			}
		}
	}

	public ValueGuiElement getValueElement(String name) {
		Optional<GuiElement> elem = guiElements.stream().filter(el -> el.name.equals(name)).findFirst();
		if (elem.isPresent()) {
			return (ValueGuiElement) elem.get();
		} else {
			throw new NullPointerException(
					"The element you are looking for: " + name + " is no gui element of this ClassGui");
		}
	}

	public String buildFunction() {
		StringBuilder sb = new StringBuilder("var " + name + " = function() {" + nl);
		guiElements.stream().forEach(elem -> sb.append("\t" + elem.preDefBuild() + nl));
		sb.append("};" + nl);
		return sb.toString();
	}

	public String build() {
		String folderName = name + "Folder";
		String objName = name + "Obj";

		StringBuilder sb = new StringBuilder("\tvar " + folderName + " = gui.addFolder('" + name + "');" + nl);
		sb.append("\tvar " + objName + " = new " + name + "();" + nl);
		guiElements.stream().forEach(elem -> sb.append("\t" + elem.build(folderName, objName) + nl));
		if (isOpen) {
			sb.append("\t" + folderName + ".open();" + nl);
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
}
