/**
 SimpleHTTPServer by Ramin Soleymani
 This example creates a HTTP Server listening on port 8000 and 
 serves the index.html file, which is located in the data folder.
 The server is accessible under the root url:
 http://localhost:8000/
 or http://127.0.0.1:8000/
 or in the network under http://<your_ip_address>:8000/
 This sketch also sets a callback function: 'callback', which is called whenever the index.html is requested 
 
 More info: http://transfluxus.github.io/SimpleHTTPServer/
 */

import http.*;

SimpleHTTPServer server;

String text="";

void setup() {
  size(400, 400);
  server = new SimpleHTTPServer(this); 
  // set the callback function for all calls (that don't have a particular callback method set- see Callback example)
  server.addCallback("","callback");
  fill(255);
  textAlign(CENTER, CENTER);
}

// all callback methods must have these two parametertypes: String,Map<String,String>
// the map contains the parameters as field-value pairs
void callback(String uri, HashMap<String, String> parameterMap) {
  println("uri:", uri, "parameters:", parameterMap); 
  if (uri.equals("/favicon.ico"))
    return;
  text = parameterMap.getOrDefault("text", "");
}

void draw() {
  background(0);
  text("The web says: "+text, width/2, height/2);
}