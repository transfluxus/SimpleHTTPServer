/**
  SimpleHTTPServer by Ramin Soleymani
  This example creates a HTTP Server listening on port 8000 and 
  serves the index.html file, which is located in the data folder.
  It is accessible under the root url:
  http://localhost:8000/
  or http://127.0.0.1:8000/
  or in the network under http://<your_ip_address>:8000/

  More info: http://transfluxus.github.io/SimpleHTTPServer/
 */
 
import http.*;

SimpleHTTPServer server;

void setup() {
  // Create a server listening on port 8000
  // serving index.html,which is in the data folder
  server = new SimpleHTTPServer(this); 
}