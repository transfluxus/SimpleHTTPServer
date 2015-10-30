import http.*;

SimpleHTTPServer server;

int red,green,blue;

void setup() {
  size(400,400);
  server = new SimpleHTTPServer(this); 
  // serve bg.html under the uri /bg, which calls setbackgroundColors
  // when query parameters are passed 
  server.serve("bg", "bg.html", "setbackgroundColors");
}

// all callback methods must have these two parametertypes: String,Map<String,String>
// the map contains the parameters as field-value pairs
void setbackgroundColors(String uri, HashMap<String, String> parameterMap) {
  println("uri:", uri, "parameters:", parameterMap); 
  red = int(parameterMap.getOrDefault("r","0"));
  green = int(parameterMap.getOrDefault("g","0"));
  blue = int(parameterMap.getOrDefault("b","0"));
}

void draw() {
  background(red,green,blue);
}