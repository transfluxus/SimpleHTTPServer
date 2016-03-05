/**
  SimpleHTTPServer by Ramin Soleymani
  This example creates a HTTP Server listening on port 8000 and 
  serves the index.html file, which is located in the data folder.
  The Server is also listening to PUT requests under the URL /echo, 
  which should contains a JSON Object with  a requestNumber value.
  The server responds with a JSON Object that contains a responseNumber.
  Check the index.html on how to create a JSON PUT request.
  
  The server is accessible under the root url:
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
  // create a custom Responsehandler with the class JSONEcho, that is defined below
  // and extends from ResponseBuilder. It returns some json.
  TemplateFileHandler responder = new ResultFromPost("params.html");
  server.createContext("params.html", responder);
}

class ResultFromPost extends TemplateFileHandler {
  
  public ResultFromPost(String templateFileName) {
    super(templateFileName);
  }
  
  void createMap(){
    
  }
  
}