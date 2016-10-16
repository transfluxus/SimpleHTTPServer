/* example of passing parameters to a webservice call which returns a JSON object
 
 Two web services: 
 fibonacci, which returns the numbers in the fibonacci series between given 'start' and 'end' values
 http://localhost:8000/fibonacci?start=1&end=100    //note that the order of the parameters does not matter
 
 squareroot, which returns the square root of the given 'value'
 example usage:  http://localhost:8000/squareroot?value=100
 
 example by Geoff Ellis.
 */

import http.*;
import java.util.Map;

final int FIBONACCI = 1;
final int SQUARE_ROOT = 2;
final int WEBSERVICE_PORT = 8000;
final String JSON_CONTENT_TYPE = "application/json";

SimpleHTTPServer server;
DynamicResponseHandler responder1, responder2;


void setup() {
  size(500, 500);
  startWebServices();
}

void draw() {
}


//------------------------------ web services  -------------------------

void startWebServices() {
  server.setLoggerLevel(java.util.logging.Level.INFO);
  server = new SimpleHTTPServer(this, WEBSERVICE_PORT); //starts service on given port

  responder1 = new DynamicResponseHandler(new TextResponse(FIBONACCI), JSON_CONTENT_TYPE);
  responder2 = new DynamicResponseHandler(new TextResponse(SQUARE_ROOT), JSON_CONTENT_TYPE);
  server.createContext("fibonacci", responder1); 
  server.createContext("squareroot", responder2);
 
}

class TextResponse extends ResponseBuilder {
  int type;

  TextResponse(int type) {
    this.type = type;
  }

  public  String getResponse(String requestBody) {
    String output = "";
    float start, end, value;
    Map<String, String> queryMap = getQueryMap();    //get parameter map as string pairs
    start = float(queryMap.getOrDefault("start", "0"));    //gets the value of the start parameter if present
    end = float(queryMap.getOrDefault("end", "10"));
    value = float(queryMap.getOrDefault("value", "4"));  //takes default value of 4 if not found
    JSONObject json = new JSONObject();
    switch (type) {
    case FIBONACCI : 
      output = getFibonacciSeries(start, end);
      break;
    case SQUARE_ROOT : 
      output = getSquareRoot(value);
      break;
    default : 
      output = "unknown type";
    }
    json.setString(getWebserviceName(type), output);
    println("responded to webservice request on /" + getWebserviceName(type) + " with parameters: " + queryMap); 
    return json.toString();  //note that javascript may require: return "callback(" + json.toString() + ")"
  }
}

String getWebserviceName(int type) {
  //returns the name of the web service
  String serviceName;
  switch (type) {
  case FIBONACCI : 
    serviceName = "fibonacci";
    break;
  case SQUARE_ROOT : 
    serviceName = "squareroot";
    break;
  default : 
    serviceName = "unknown (" + type + ")";
  }
  return serviceName;
}

//--------------------- handlers ---------------------

String getFibonacciSeries(float startOfRange, float endOfRange) {
  //returns the Fibonacci series which occur within the set range
  String output = "";
  float value = 1, lastValue = 0, nextValue;
  while (value < startOfRange) { 
    nextValue = value + lastValue;
    lastValue = value;
    value = nextValue;
  }
  while (value <= endOfRange) {
    output += output.length() > 0 ? ", " : ""; //comma separated list 
    output += int(value);
    nextValue = value + lastValue;
    lastValue = value;
    value = nextValue;
  }
  return output;
}

String getSquareRoot(float value) {
  //returns the square root of the value
  return str(sqrt(value));
}