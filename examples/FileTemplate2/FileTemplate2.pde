/**
 This example shows the basic use of using query parameters and
 the use template files. The output file is generated with Apache FreeMarker
 template engine.
 http://freemarker.incubator.apache.org/
 In order to create a page from a template, write a class
 deriving from TemplateFileHandler.
 Then implement the Method void createMap()
 in which you can access the query parameters from the map
 Map<String, String> params = queryToMap();
 The Apache FreeMarker is very powerfull and I only use simple replacement:
 ${result} in the template is replaced by multiplication of the query parameters a and b.
 So http://localhost:8000/?a=10&b=2
 returns a page with the result 20
 
 The server is accessible under the root url:
 http://localhost:8000/
 or http://127.0.0.1:8000/
 or in the network under http://<your_ip_address>:8000/
 
 More info: http://transfluxus.github.io/SimpleHTTPServer/
 */

import http.*;
import java.util.Map;

SimpleHTTPServer server;

void setup() {
  // prevents to create the default context for path "" and file data/index.html 
  SimpleHTTPServer.useIndexHtml = false;
  // Create a server listening on port 8000
  // serving index.html,which is in the data folder
  server = new SimpleHTTPServer(this); 
  // create a custom Handler, which is a subclass of TemplateFileHandler
  // TemplateFileHandler gets a templatefile passed, which should be in the data folder
  TemplateFileHandler templateHandler = new ResultFromPost("index.ftl");
  // Create a context for the basepath and the templateHandler
  server.createContext("", templateHandler);
}

class ResultFromPost extends TemplateFileHandler {

  public ResultFromPost(String templateFileName) {
    super(templateFileName);
  }

  void createMap() {
    Map<String, String> params = queryToMap();
    float result = 0;
    // calculate the result when both parameter a and b are there
    if (params.containsKey("a") && params.containsKey("b")) {
      try {
        result = Float.valueOf(params.get("a")) * Float.valueOf(params.get("b"));
      } 
      catch(Exception exc) {
        println("parameters are probably no numbers");
      }
    }
    // add the result to the model, which will be used to process the template
    addVariable("result", result);
  }
}