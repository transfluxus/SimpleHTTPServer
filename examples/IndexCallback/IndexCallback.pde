import http.*;

SimpleHTTPServer server;

String text="";

void setup() {
  size(400,400);
  server = new SimpleHTTPServer(this); 
  // set the callback function for all calls (that don't have a particular callback method set- see Callback example)
  server.setIndexCallback("callback");
  fill(255);
  textAlign(CENTER,CENTER);
}

// all callback methods must have these two parametertypes: String,Map<String,String>
// the map contains the parameters as field-value pairs
void callback(String uri, HashMap<String, String> parameterMap) {
  println("uri:", uri, "parameters:", parameterMap); 
  text = parameterMap.getOrDefault("text","");
}

void draw() {
  background(0);
  text("The web says: "+text,width/2,height/2);
}