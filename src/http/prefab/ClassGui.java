package http.prefab;

import http.prefab.guiElement.GuiElement;
import http.prefab.guiElement.ValueGuiElement;
import http.prefab.guiElement.ValueGuiElement.TYPE;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ClassGui {

	private final Class<?> clazz;

	public boolean isOpen = true;

	private List<GuiElement> guiElements = new ArrayList<>();

	static String nl = System.getProperty("line.separator");

	public ClassGui(Class<?> clazz) {
		this.clazz = clazz;
		allPublics();
	}

	public void allPublics() {
		Field[] fields = clazz.getDeclaredFields();
		Optional<Object> tempObj = null;
		try {
			tempObj = Optional.of(clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("The class: " + clazz.getSimpleName()
					+ " should provide a public default constructor for the default values");
		}
		for (Field field : fields) {
			if (!Modifier.isPublic(field.getModifiers()))
				continue;
			String name = field.getName();
			Optional<? extends GuiElement> element = Optional.empty();
			try {
				if (field.getType().equals(Integer.TYPE)) {
					String defaultValue = "0";
					if (tempObj.isPresent())
						defaultValue = String.valueOf(field.getInt(tempObj.get()));
					element = Optional.of(new ValueGuiElement(name, TYPE.INT, defaultValue).step(1));
				} else if (field.getType().equals(Float.TYPE)) {
					String defaultValue = "0.1";
					defaultValue = String.valueOf(field.getFloat(tempObj.get()));
					element = Optional.of(new ValueGuiElement(name, TYPE.FLOAT, defaultValue));
				} else if (field.getType().equals(Boolean.TYPE)) {

				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (element.isPresent()) {
				guiElements.add(element.get());
			}
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
		StringBuilder sb = new StringBuilder("var " + clazz.getSimpleName() + " = function() {" + nl);
		guiElements.stream().forEach(elem -> sb.append("\t" + elem.preDefBuild() + nl));
		sb.append("};" + nl);
		return sb.toString();
	}

	public String build() {
		String name = clazz.getSimpleName();
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
}
