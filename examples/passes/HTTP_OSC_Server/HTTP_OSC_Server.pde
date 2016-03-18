import http.*;
import http.prefab.*;
import passthrough.httpToOsc.*;
import java.util.Optional;

import java.io.FileNotFoundException;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;

import oscP5.*;
import netP5.*;

SimpleHTTPServer httpServer;
OscP5 oscP5;
HTTP_OSC passthrough;

// remote port
int port = 57120;


void setup() {
  size(500, 500);
  // prevents initial "" to index.html simpleFileHandler
  //SimpleHTTPServer.useIndexHtml = false;
  SimpleHTTPServer.setLoggerLevel(java.util.logging.Level.INFO);
  httpServer = new SimpleHTTPServer(this);
  httpServer.serve("action.js");
  /*
  try {
   HttpHandler handler = httpServer.getContext("index.html").getHandler();
   httpServer.createContext("", handler);
   } 
   */

  oscP5 = new OscP5(this, 12000);
  passthrough = new HTTP_OSC(httpServer, oscP5);

  passthrough.addLocalAddress(port);

  // creating the synth
  Passthrough_ResponseBuilder responseBuilder = new Passthrough_ResponseBuilder("/synth/new", port);
  responseBuilder.addQueryPassFloat("freq");
  responseBuilder.addQueryPassFloat("amp");
  httpServer.createAppContext("pass/synth/new", responseBuilder);

  // updating the synth
  Passthrough_ResponseBuilder responseBuilderUpdate = new Passthrough_ResponseBuilder("/synth/update", port);
  // this is a simply function to create float passes
  responseBuilderUpdate.addQueryPassFloat("freq");
  /* This is a long version of the same call.
   There are two possible Transmission types: Query (query parameters) and App_json (JSON data in the request body)
   There are three OscMsgType supported right now: i (int), f (float) and s (String)
   The last parameters sets a defaultvalue in case a value is not sent with the request.
   
   // no defaultvalue
   responseBuilderUpdate.addValPass("freq", Passthrough_ResponseBuilder.TransmissionType.Query, 
   Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f);
   
   // defaultvalue 100
   Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f,100);
   */

  responseBuilderUpdate.addQueryPassFloat("amp");
  httpServer.createAppContext("pass/synth/update", responseBuilderUpdate);
}


void draw() {
  background(0);
}