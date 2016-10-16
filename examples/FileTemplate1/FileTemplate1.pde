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
 ${user} in the template is replaced by the query parameter user.
 Use 
 localhost:8000/?user=Ramin
 returns a page saying "Hello Ramin".
 If the parameter user does not exist in the query it will say: 
 "Hello unknown user"
 
 
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
    String user = "unknown user";
    if (params.containsKey("user")) {
      user = params.get("user");
    }
    // add the user to the model, which will be used to process the template
    addVariable("user", user);
  }
}