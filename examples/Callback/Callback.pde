/**
  SimpleHTTPServer by Ramin Soleymani
  This example creates a HTTP Server listening on port 8000 and 
  serves the index.html file, which is located in the data folder.
  It also checks for query parameters under the path /bg.
  The sketch sets a callback function for /bg requests which is called 'setbackgroundColors'
  The function is called whenever /bg is requested and checks for the parameter of the call. 
  It checks for the parameters: r,g,b, which controll the background color of the processing frame.
  Examples would be 
  http://127.0.0.1:8000/bg.html?r=255&g=128&b=0
  or
  http://127.0.0.1:8000/bg.html?b=200
  or
  http://localhost:8000/bg?b=230&g=200 (the order of r,g,b doesn't matter)
  
  The Server is accessible under the root url:
  http://localhost:8000/
  or http://127.0.0.1:8000/
  or in the network under http://<your_ip_address>:8000/

  More info: http://transfluxus.github.io/SimpleHTTPServer/
 */

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
  println("uri:", uri, "parameters:");
  println(parameterMap); 
  red = int(parameterMap.getOrDefault("r","0"));
  green = int(parameterMap.getOrDefault("g","0"));
  blue = int(parameterMap.getOrDefault("b","0"));
}

void draw() {
  background(red,green,blue);
}