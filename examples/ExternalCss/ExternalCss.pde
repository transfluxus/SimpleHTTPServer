/**
  SimpleHTTPServer by Ramin Soleymani
  This example creates a HTTP Server listening on port 8000 and 
  serves the index.html file, which is located in the data folder.
  It is accessible under the root url:
  http://localhost:8000/
  or http://127.0.0.1:8000/
  or in the network under http://<your_ip_address>:8000/
  It also provides the style.css file, which is requested by the index.html
  and another html file: hello.html under /hello.
  
  More info: http://transfluxus.github.io/SimpleHTTPServer/
 */
 
import http.*;

SimpleHTTPServer server;

void setup() {
  // Create a server listening on port 8000
  // serving index.html,which is in the data folde
  server = new SimpleHTTPServer(this); 
  // also serves style.css, which is used in index.html
  server.serve("style.css");
  server.serve("hello","hello.html");
}