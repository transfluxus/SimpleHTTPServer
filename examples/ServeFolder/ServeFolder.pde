import http.*;
import java.util.logging.Level;

SimpleHTTPServer server;

void setup() {
  // set the logger level to info   
  server.setLoggerLevel(Level.INFO);
  // create a server 
  server = new SimpleHTTPServer(this);
  // serve all files recursively in the data folder with the base uri path ""
  server.serveAll("");
  // to add callback to already existing contexts use the addCallback method with the uriPath and the methodname
  server.addCallback("sub/index.html", "sub");
  // serve file from an absolute 
  server.serveAll("web/",sketchPath()+"/web");
}

// all callback methods must have these two parametertypes: String,Map<String,String>
// the map contains the parameters as field-value pairs
void sub(String uri, HashMap<String, String> parameterMap) {
  println("someone accessed the sub/index.html");
}

public void draw() {
}