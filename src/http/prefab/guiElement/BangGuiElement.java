package http.prefab.guiElement;

public class BangGuiElement extends GuiElement {

	public BangGuiElement(String name) {
		super(name);
	}

	@Override
	public String preDefBuild() {
		return "this."+name+" = function() { "+" };";
	}

	@Override
	public String build(String addTo,String objName) {
		// TODO Auto-generated method stub
		return null;
	}

}
