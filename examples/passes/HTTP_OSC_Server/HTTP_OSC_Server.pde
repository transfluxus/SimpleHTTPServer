import http.*;
import http.prefab.*;
import passthrough.httpToOsc.*;
import java.util.Optional;

import oscP5.*;
import netP5.*;

SimpleHTTPServer httpServer;
OscP5 oscP5;
HTTP_OSC passthrough;

int port = 8000;

//ArrayList<Synth> synths = new ArrayList<Synth>();

void setup() {
  size(500, 500);
  noStroke();
  /* start oscP5, listening for incoming messages at port 12000 */

SimpleHTTPServer.setLoggerLevel(java.util.logging.Level.INFO);
  httpServer = new SimpleHTTPServer(this);
  httpServer.serveAll("");
  oscP5 = new OscP5(this, 12000);
  passthrough = new HTTP_OSC(httpServer, oscP5);

  passthrough.addLocalAddress(port);

  Passthrough_ResponseBuilder responseBuilder = new Passthrough_ResponseBuilder(); 
  responseBuilder.autoSend(passthrough.getLocalAddress().get());
  responseBuilder.addValPass("freq", Passthrough_ResponseBuilder.TransmissionType.Query, 
  Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType.f, Optional.empty());
  httpServer.createAppContext("pass/synth/new",responseBuilder);
}


void draw() {
  background(0);
 }

/*

void oscEvent(OscMessage oscMsg) {
  String addr = oscMsg.addrPattern();
  String typeTag = oscMsg.typetag();
  switch(addr) {
  case "/synth/new": 
    {
      // f: freq, f: amp
      oscMsg.add(
    }
    break;
  case "/synth/update":
    PVector speed = getPVectorFromIndex(oscMsg, 1);
    getBallFromId(oscMsg).speed = speed;
}
*/
/*
Synth getSynthFromId(OscMessage msg) {
  return synths.get(msg.get(0).intValue());
}
*/

/*
class Synth {
  float freq;
  float amp;


  public Synth(float freq, float amp) {
    this.freq = freq;
    this.amp = amp;
  }

  void update() {

  }
}
*/