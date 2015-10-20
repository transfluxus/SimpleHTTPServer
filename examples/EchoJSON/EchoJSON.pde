import http.*;

SimpleHTTPServer server;

void setup() {
  // Create a server listening on port 8000
  // serving index.html,which is in the data folder
  server = new SimpleHTTPServer(this); 
  // create a custom Responsehandler with the class JSONEcho, that is defined below
  // and extends from ResponseBuilder. It returns some json.
  DynamicResponseHandler responder = new DynamicResponseHandler(new JSONEcho(), "application/json");
  // It responds to a PUT on url /echo

  server.createContext("echo", responder);
}

class JSONEcho extends ResponseBuilder {

  
  
  /**
  This abstract function needs to be overwritten. 
  In this example the json request needs to include a requestNumber.
  It then returns the same JSONObject but adds responseNumber,which is the double
  of requestNumber
  */
  public  String getResponse(String requestBody) {
    JSONObject json = parseJSONObject(requestBody);
    int number = json.getInt("requestNumber");
    json.setInt("responseNumber", number*2);
    return json.toString();
  }
}