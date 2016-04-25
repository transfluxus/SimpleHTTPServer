import http.*;
import http.prefab.*;

import oscP5.*;
import netP5.*;

HttpServer httpServer;
OscP5 oscP5;
HTTP_OSC passthrough;

int port = 8000;

ArrayList<Synth> synths = new ArrayList<Synth>();

void setup() {
  size(500, 500);
  noStroke();
  /* start oscP5, listening for incoming messages at port 12000 */

  httpServer = new SimpleHTTPServer(this); 
  oscP5 = new OscP5(this, 12000);
  passthrough = new HTTP_OSC(httpServer, oscP5);

  passthrough.addLocalAddress(port);

  Passthrough_ResponseBuilder responseBuilder = new Passthrough_ResponseBuilder(); 
  responseBuilder.autoSend(passthrough.getLocalAddress());
  responseBuilder.addValPass("freq", TransmissionType.APP_JSON, OscMsgType.f, Optional.empty());
}


void draw() {
  background(0);
  for (Synth synth : synths) {
    synth.update();
  }
}


/* incoming osc message are forwarded to the oscEvent method. */
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
  case "/ball/size":
    speed = getPVectorFromIndex(oscMsg, 1);
    getBallFromId(oscMsg).speed = speed;
    break;
  case "/ball/color":
    color clr = getColorFromIndex(oscMsg, 1);
    getBallFromId(oscMsg).clr = clr;
    break;
  }
}

Ball getBallFromId(OscMessage msg) {
  return balls.get(msg.get(0).intValue());
}



class Synth {
  float freq;
  float amp;


  public Ball(float freq, float amp) {
    this.freq = freq;
    this.amp = amp;
  }

  void update() {
    pos.add(speed);
    pos.x = (pos.x + width) % width;
    pos.y = (pos.y + height) % height;
    fill(clr);
    ellipse(pos.x, pos.y, size, size);
  }
}