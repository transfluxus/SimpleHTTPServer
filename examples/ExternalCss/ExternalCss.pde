import http.*;

SimpleHTTPServer server;

void setup() {
  // Create a server listening on port 8000
  // serving index.html,which is in the data folde
  server = new SimpleHTTPServer(this); 
  // also serves style.css, which is used in index.html
  server.serve("style.css");
  server.serve("hello2.html");
}