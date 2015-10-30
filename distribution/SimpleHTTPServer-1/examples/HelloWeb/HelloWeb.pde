import http.*;

SimpleHTTPServer server;

void setup() {
  // Create a server listening on port 8000
  // serving index.html,which is in the data folder
  server = new SimpleHTTPServer(this); 
}