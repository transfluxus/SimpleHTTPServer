package passthrough.httpToOsc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import http.SimpleHTTPServer;
import netP5.NetAddress;
import oscP5.OscP5;

public class HTTP_OSC {

	SimpleHTTPServer httpServer;
	OscP5 oscP5;

	Map<String, NetAddress> getRemoteAddresses = new HashMap<>();

	protected static Logger logger = Logger.getLogger("Passthrough");

	public HTTP_OSC(SimpleHTTPServer httpServer) {
		this(httpServer, null);
		this.httpServer = httpServer;
	}

	public HTTP_OSC(oscP5.OscP5 oscP5) {
		this(null, oscP5);
	}

	public HTTP_OSC(SimpleHTTPServer httpServer, OscP5 oscP5) {
		this.httpServer = httpServer;
		this.oscP5 = oscP5;
		Passthrough_ResponseBuilder.parent = this;
	}

	public void addLocalAddress(int port) {
		getRemoteAddresses.put("localhost", new NetAddress("127.0.0.1", port));
	}

	public Optional<NetAddress> getLocalAddress() {
		return getRemoteAddress("localhost");
	}

	public Optional<NetAddress> getRemoteAddress(String hostName) {
		if (getRemoteAddresses.containsKey(hostName)) {
			return Optional.of(getRemoteAddresses.get(hostName));
		} else {
			logger.warning("HTTP OSC doesn't have a host addressed saved with the name " + hostName);
			return Optional.empty();
		}
	}

//	public DynamicResponseHandler createDynamicResponseHanlder(Map<String, Integer> argumentIndices) {
//		String adressPattern;
//		// DynamicResponseHandler handler = new
//		// DynamicResponseHandler(responseBuilder, "text/html")
//	}

}
