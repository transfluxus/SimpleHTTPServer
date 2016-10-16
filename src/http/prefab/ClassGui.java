package http.prefab;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import http.prefab.ValueGuiElement.TYPE;

public class ClassGui {

	private final Class<?> clazz;

	public final String name;

	public boolean isOpen = true;

	private List<GuiElement> guiElements = new ArrayList<>();

	static String nl = System.getProperty("line.separator");

	private final Object relatedObject;
	
	protected static Logger logger = Logger.getLogger("datguiLogger");

	protected ClassGui(Class<?> clazz, Object relatedObject, int id) {
		this.clazz = clazz;
		this.name = clazz.getSimpleName() + id;
		// System.out.println("CG: "+name);
		this.relatedObject = relatedObject;
	}

	/**
	 * add all public fields of the related Class to the Classgui
	 */
	public void allPublics() {
		for (Field field : clazz.getDeclaredFields()) {
			addFieldGui(field.getName());
		}
	}

	public GuiElement addFieldGui(String fieldName) {
		Optional<Field> fieldOpt = getField(fieldName);
		if (!fieldOpt.isPresent()) {
			System.err.println("Field is not available");
			return null;
		}
		Field field = fieldOpt.get();
		Class<?> type = field.getType();
		Optional<? extends GuiElement> element = Optional.empty();
		// System.out.println(field);
		try {
			if (type.equals(Integer.TYPE)) {
				String defaultValue = "0";
				defaultValue = String.valueOf(field.getInt(relatedObject));
				element = Optional.of(new ValueGuiElement(fieldName, TYPE.INT, defaultValue).step(1));
			} else if (type.equals(Float.TYPE)) {
				String defaultValue = "0.1";
				defaultValue = String.valueOf(field.getFloat(relatedObject));
				element = Optional.of(new ValueGuiElement(fieldName, TYPE.FLOAT, defaultValue));
			} else if (type.equals(Boolean.TYPE)) {
				String defaultValue = "false";
				defaultValue = String.valueOf(field.getBoolean(relatedObject));
				element = Optional.of(new BoolGuiElement(fieldName, defaultValue));
			} else {
				logger.info("The field: " + fieldName + " has a unsupported type (supported are int,float,bool)");
				// System.out.println(field.getType());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (element.isPresent()) {
			guiElements.add(element.get());
			return element.get();
		} else {
			logger.warning("guiElement for field': "+fieldName+"' of class: '"+name+"' could not be created");
			return null;
		}
	}
	
	public void addFieldGui(String fieldName,float min) {
		GuiElement element = addFieldGui(fieldName);
		if(element == null)
			return;
		if(element.getClass().equals(ValueGuiElement.class)) {
			ValueGuiElement valueGuiElement = (ValueGuiElement) element;
			valueGuiElement.min(min);
		} else {
			logger.warning("Couldn't define min and max of element since the field is not int nor float");
		}
	}
	
	public void addFieldGui(String fieldName,float min,float max) {
		GuiElement element = addFieldGui(fieldName);
		if(element == null)
			return;
		if(element.getClass().equals(ValueGuiElement.class)) {
			ValueGuiElement valueGuiElement = (ValueGuiElement) element;
			valueGuiElement.min(min);
			valueGuiElement.max(max);
		} else {
			logger.warning("Couldn't define min and max of element since the field is not int nor float");
		}
	}

	public void addSelector(String arrayName, int defaultIndex, String targetFieldName) {

		// get the array with options and the type (should be array)
		Optional<Field> fieldOpt = getField(arrayName);
		if (!fieldOpt.isPresent())
			return;
		Field field = fieldOpt.get();
		Class<?> type = field.getType();

		// get the targetfield and its type (should be string)
		Optional<Field> targetFieldOpt = getField(targetFieldName);
		if (!targetFieldOpt.isPresent())
			return;
		Field targetField = targetFieldOpt.get();
		Class<?> targetType = targetField.getType();
		// if one necessary condition (1: array of strings and 2: target =
		// string > bye
		if (!type.isArray() || !type.getComponentType().equals(String.class) || !targetType.equals(String.class)) {
			logger.warning("Selector with: "+arrayName +" > "+targetFieldName+ " in class: "+name+" can not be created. Array must be array of Strings, target must be a String"
					);
			return;
		}

		try {
			// set the default value & prepare array for the guiElement and add it
			String[] vals = ((String[]) field.get(relatedObject)).clone();
			targetField.set(relatedObject, vals[defaultIndex]);
			for (int i = 0; i < vals.length; i++) {
				vals[i] = "'" + vals[i] + "'";
			}
			if (vals.length > 0 && vals[0] != null) {
				String values = Arrays.toString(vals);
				guiElements.add(new StringListElement(arrayName, vals[defaultIndex], values,targetFieldName));
			}
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private Optional<Field> getField(String fieldName) {
		try {
			Field field = clazz.getField(fieldName);
			if (!Modifier.isPublic(field.getModifiers())) {
				System.err.println("Field with name: " + field.getName() + " for class: " + clazz.getSimpleName()
						+ " is not public");
				return Optional.empty();
			}
			if(Modifier.isFinal(field.getModifiers())) {
				logger.warning("Field with name: " + field.getName() + " for class: " + clazz.getSimpleName()
				+ " is final and cannot be edited");
				return Optional.empty();
			}
			return Optional.of(field);
		} catch (NoSuchFieldException | SecurityException e) {
			System.err.println("Field with name: " + fieldName + " does not exist for class: " + clazz.getSimpleName());
			return Optional.empty();
		}
	}

	public void addMethodTrigger(String methodName) {
		try {
			clazz.getMethod(methodName, new Class<?>[] {});
			guiElements.add(new BangGuiElement(name, methodName));
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("Cannot find or acces method: " + methodName + " in class: " + name);
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
					"The element you are looking for: '" + name + "' is no gui element of this ClassGui");
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

	public void setBoundary(String valueName, float min, float max) {
		getValueElement(valueName).min(min).max(max);
	}

	public Object getRelatedObject() {
		return relatedObject;
	}
	
}
