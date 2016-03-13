package pass;

import java.io.FileNotFoundException;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;

import http.SimpleHTTPServer;
import oscP5.OscP5;
import passthrough.httpToOsc.HTTP_OSC;
import passthrough.httpToOsc.Passthrough_ResponseBuilder;
import processing.core.PApplet;

public class PassServer extends PApplet {

	SimpleHTTPServer httpServer;
	OscP5 oscP5;
	HTTP_OSC passthrough;

	// remote port
	int port = 57120;

	// ArrayList<Synth> synths = new ArrayList<Synth>();

	public void setup() {
		// noStroke();
		// only for eclipse
		SimpleHTTPServer.useIndexHtml = false;
		SimpleHTTPServer.setLoggerLevel(java.util.logging.Level.INFO);
		httpServer = new SimpleHTTPServer(this);
		httpServer.serveAll("",sketchPath()+"/test-src/pass/data");
		try {
			HttpHandler handler = httpServer.getContext("index.html").getHandler();
			httpServer.createContext("",handler);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			httpServer.printAllContexts();
		}
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

	public void draw() {
		background(0);
	}

	/*
	 * 
	 * void oscEvent(OscMessage oscMsg) { String addr = oscMsg.addrPattern();
	 * String typeTag = oscMsg.typetag(); switch(addr) { case "/synth/new": { //
	 * f: freq, f: amp oscMsg.add( } break; case "/synth/update": PVector speed
	 * = getPVectorFromIndex(oscMsg, 1); getBallFromId(oscMsg).speed = speed; }
	 */
	/*
	 * Synth getSynthFromId(OscMessage msg) { return
	 * synths.get(msg.get(0).intValue()); }
	 */

	/*
	 * class Synth { float freq; float amp;
	 * 
	 * 
	 * public Synth(float freq, float amp) { this.freq = freq; this.amp = amp; }
	 * 
	 * void update() {
	 * 
	 * } }
	 */

	public static void main(String[] args) {
		PApplet.main("pass.PassServer");
	}
}
