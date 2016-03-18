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

  Passthrough_ResponseBuilder responseBuilder = new Passthrough_ResponseBuilder("/synth/new");
  responseBuilder.autoSend(passthrough.getLocalAddress().get());
  responseBuilder.addValPass("freq", Passthrough_ResponseBuilder.TransmissionType.Query, 
    Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f, Optional.empty());
  responseBuilder.addValPass("amp", Passthrough_ResponseBuilder.TransmissionType.Query, 
    Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f, Optional.empty());
  responseBuilder.autoSend(passthrough.getLocalAddress().get());
  httpServer.createAppContext("pass/synth/new", responseBuilder);


  Passthrough_ResponseBuilder responseBuilderUpdate = new Passthrough_ResponseBuilder("/synth/update");
  responseBuilderUpdate.autoSend(passthrough.getLocalAddress().get());
  responseBuilderUpdate.addValPass("freq", Passthrough_ResponseBuilder.TransmissionType.Query, 
    Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f, Optional.empty());
  responseBuilderUpdate.addValPass("amp", Passthrough_ResponseBuilder.TransmissionType.Query, 
    Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f, Optional.empty());
  responseBuilderUpdate.autoSend(passthrough.getLocalAddress().get());
  httpServer.createAppContext("pass/synth/update", responseBuilderUpdate);
}


void draw() {
  background(0);
}