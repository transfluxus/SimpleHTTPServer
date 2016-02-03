package http.prefab.guiElement;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class FieldGuiElement extends GuiElement {

	Field field;
	
	@Override
	public Optional<GuiElement> FromField(Field field, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String preDefBuild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String build(String addTo, String objectName) {
		// TODO Auto-generated method stub
		return null;
	}

}
