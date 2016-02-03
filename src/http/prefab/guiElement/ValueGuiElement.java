package http.prefab.guiElement;

public class ValueGuiElement extends GuiElement {

	public enum TYPE {INT,FLOAT};
	
	public final TYPE type;
	public float min = Float.NaN;
	public float max = Float.NaN;
	public float step = Float.NaN;
	
	public ValueGuiElement(String name, TYPE type) {
		this(name,type,"0");
	}
	
	public ValueGuiElement(String name, TYPE type,String defaultValue) {
		super(name,defaultValue);
		this.type = type;
	}

	public ValueGuiElement step(float stepSize) {
		step = Math.abs(stepSize);
		return this;
	}

	public ValueGuiElement min(float min) {
		this.min = min;
		return this;
	}
	
	public ValueGuiElement max(float max) {
		this.max = max;
		return this;
	}

	public String preDefBuild() {
		String val = (type == TYPE.INT ? 
				""+Integer.parseInt(defaultValue) : 
					""+Float.parseFloat(defaultValue));
		if(type == TYPE.FLOAT && Float.parseFloat(defaultValue) % 1.0 == 0) {
			System.err.println("Better give float values a non integer default value. otherwise dat.gui will display it as an integer");
		}
		String ret =  "this."+name+ " = " +val+";";
		return ret;
	}
	
	
	public String build(String addTo,String objectName) {
		StringBuilder sb= new StringBuilder(addTo+".add("+objectName+", '"+name+"')");
		String className = objectName.substring(0,objectName.length()-3);
		// thats the way to get if a value is not NaN
		if(min == min) {
			sb.append(".min("+ (type == TYPE.INT ? (int)min : min) + ")");
		} 
		if (max == max) {
			sb.append(".max("+ (type == TYPE.INT ? (int)max : max) + ")");
		} 
		if (step == step) {
			sb.append(".step("+ (type == TYPE.INT ? ""+(int)step : step) + ")");
		}
		sb.append(".onChange(function(value) { addToUpdate('"+className+"','"+name+"',"+objectName+"."+name+"); });");
		return sb.toString();
	}
}
