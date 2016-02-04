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

	private final Object relatedObject;

	public ClassGui(Class<?> clazz, Object relatedObject, int id) {
		this.clazz = clazz;
		this.name = clazz.getSimpleName() + id;
		// System.out.println("CG: "+name);
		this.relatedObject = relatedObject;
	}

	public void allPublics() {
		for (Field field : clazz.getDeclaredFields()) {
			addFieldGui(field);
		}
	}

	public void addFieldGui(String fieldName) {
		try {
			addFieldGui(clazz.getField(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			System.err.println("Field with name: " + fieldName + " does not exist for class: " + clazz.getSimpleName());
		}
	}

	public void addFieldGui(Field field) {
		if (!Modifier.isPublic(field.getModifiers())) {
			System.err.println(
					"Field with name: " + field.getName() + " for class: " + clazz.getSimpleName() + " is not public");
			return;
		}
		String name = field.getName();
		Optional<? extends GuiElement> element = Optional.empty();
		// System.out.println(field);
		try {
			Class<?> type = field.getType();
			if (type.equals(Integer.TYPE)) {
				String defaultValue = "0";
				defaultValue = String.valueOf(field.getInt(relatedObject));
				element = Optional.of(new ValueGuiElement(name, TYPE.INT, defaultValue).step(1));
			} else if (type.equals(Float.TYPE)) {
				String defaultValue = "0.1";
				defaultValue = String.valueOf(field.getFloat(relatedObject));
				element = Optional.of(new ValueGuiElement(name, TYPE.FLOAT, defaultValue));
			} else if (type.equals(Boolean.TYPE)) {
				String defaultValue = "false";
				defaultValue = String.valueOf(field.getBoolean(relatedObject));
				element = Optional.of(new BoolGuiElement(name, defaultValue));
			} else if (type.isArray() && (
			// type.getComponentType().isPrimitive() ||
			type.getComponentType().equals(String.class))) {
				String[] vals = (String[]) field.get(relatedObject);
				for (int i = 0; i < vals.length; i++) {
					vals[i] = "'" + vals[i] + "'";
				}
				if (vals.length > 0 && vals[0] != null) {
					String values = Arrays.toString(vals);
					element = Optional.of(new StringListElement(name, vals[0], values));
				}
			} else {
				// System.out.println(field.getType());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (element.isPresent()) {
			guiElements.add(element.get());
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
