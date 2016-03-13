package passthrough.httpToOsc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import http.ResponseBuilder;
import http.SimpleHTTPServer;
import netP5.NetAddress;
import oscP5.OscMessage;
import passthrough.httpToOsc.Passthrough_ResponseBuilder.OSCMessageEntry.OscMsgType;
import processing.data.JSONObject;

public class Passthrough_ResponseBuilder extends ResponseBuilder {

	public static HTTP_OSC parent;

	private List<HttpHandler> msgEntries = new ArrayList<>();
	private String addressPattern;
	private String msgType;
	private int msglength;

	protected static Logger logger = Logger.getLogger("Passthrough");

	// TODO
	// setter with constr.
	private boolean doBuffer = false;
	List<OscMessage> msgBuffer;
	// max buffer sz?

	// if thats set we gonna send them out immediately
	private boolean autoSend;
	private NetAddress netAddress;
	public static int DEFAULT_PORT = 5600;
	// public int port = DEFAULT_PORT;

	// Optional<HttpHandler> remoteIPAdapter = Optional.empty();

	Optional<OscMessageOut> MsgOut = Optional.empty();

	String actRequestBody;

	public Passthrough_ResponseBuilder(String addressPattern) {
		this.addressPattern = addressPattern;
	}

	public void addValPass(String valName, TransmissionType type, OscMsgType oscType, Optional<Object> defaultValue) {
		OSCMessageEntry oscMsg = new OSCMessageEntry(oscType);
		HttpHandler handler = new HttpHandler(valName, type, oscMsg);
		msgEntries.add(handler);
		msgType += oscMsg.getTypeString();
		msglength++;
	}

	public void autoSendLocal(int port) {
		autoSend("127.0.0.1", port);
	}

	public void autoSend(String ipAddr, int port) {
		autoSend(new NetAddress(ipAddr, port));
	}

	public void autoSend(NetAddress address) {
		netAddress = address;
		autoSend = true;
	}

	public void setAutoSend(boolean autoSend) {
		if (netAddress != null) {
			this.autoSend = autoSend;
		} else {
			logger.warning("Passthrough. cannot set autosend before setting NetAddress. call: autoSend");
		}
	}

	// for getting ip and port from the msg
	// void addRemoteIPAdapter(String valName, TransmissionType type) {
	// remoteIPAdapter = Optional.of(new HttpHandler(valName, type, null));
	// }

	// void addPortAdapter(String valName,TransmissionType type) {
	// portAdapter = new HttpHandler(valName, type, null);
	// }

	void removeValPass(int index) {
		msgType = msgType.substring(0, index) + msgType.substring(index + 1);
		msglength--;
	}

	public enum TransmissionType {
		Query, APP_JSON
	}

	@Override
	public String getResponse(String requestBody) {
		this.actRequestBody = requestBody;
		System.out.println(requestBody);
		JSONObject jsonReturn = new JSONObject();
		Object[] values = new Object[msglength];
		int i = 0;
		// get the value of each request either from the query or as ajax
		// System.out.println("RB2");
		for (HttpHandler handler : msgEntries) {
			Optional<Object> value = getValue(handler, requestBody);
			if (value.isPresent()) {
				values[i++] = value.get();
			} else {
				jsonReturn.setBoolean("created", false);
				return jsonReturn.toString();
			}
		}
		// System.out.println("RB3");
		OscMessage oscMsg = new OscMessage(addressPattern, values);
		autoSend(oscMsg);
		// directSend(oscMsg)
		jsonReturn.setBoolean("created", true);
		return jsonReturn.toString();
	}

	public void autoSend(OscMessage msg) {
		if (autoSend) {
			parent.oscP5.send(msg, netAddress);
		} else {
			logger.warning("Can't send OscMessage.Remote address not defined");
		}
	}

	// for getting ip and port from the message itself. do later
	// private void directSend(OscMessage msg) {
	// if (remoteIPAdapter.isPresent()) {
	// Optional<Object> ipAdr = getValue(remoteIPAdapter.get(),
	// this.actRequestBody);
	// if (ipAdr.isPresent()) {
	// String remoteAddress = (String) ipAdr.get();
	// parent.oscP5.send(msg, new NetAddress(remoteAddress, this.port));
	// }
	// }
	// }

	private Optional<Object> getValue(HttpHandler handler, String requestBody) {
		Optional<String> strValue = Optional.empty();
		if (handler.type == TransmissionType.Query) {
			Map<String, String> queryMap = getQueryMap();
			if (queryMap.containsKey(handler.name)) {
				strValue = Optional.of(queryMap.get(handler.name));
			}
		} else { // handler.type == TransmissionType.APP_JSON
			JSONObject json = SimpleHTTPServer.parent.parseJSONObject(requestBody);
			if (json.hasKey(handler.name)) {
				// todo: whats when this is int already (or other type)
				strValue = Optional.of(json.getString(handler.name));
			}
		}
		// add the value to build the OSCMessage
		if (strValue.isPresent()) {
			return Optional.of(handler.getValue(strValue.get()));
			// no value in the request
			// fallback to default value
		} else {
			if (handler.hasDefault()) {
				return Optional.of(handler.getDefault());
			} else { // not in the request and no default. you should think
				logger.severe(
						"OscMessage will not be sent or buffered. There is an argument missing in order to build the OSC message. "
								+ "The Responsebuilder is missing::: " + handler.name
								+ ". Set a default value or remove the HttpHandler msgEntries that looks for: "
								+ handler.name);
				return Optional.empty();
			}
		}
	}

	public static class HttpHandler {

		public final String name;
		public TransmissionType type;
		public OSCMessageEntry msgEntry;

		public HttpHandler(String name, TransmissionType type, OSCMessageEntry msgEntry) {
			this.name = name;
			this.type = type;
			this.msgEntry = msgEntry;
		}

		public Object getValue(String strValue) {
			return msgEntry.getValue(strValue);
		}

		public boolean hasDefault() {
			return msgEntry.defaultValue.isPresent();
		}

		/**
		 * use only after hasDefault!
		 * 
		 * @return
		 */
		public Object getDefault() {
			return msgEntry.defaultValue.get();
		}

	}

	public static class OSCMessageEntry {

		final OscMsgType type;
		Optional<Object> defaultValue;

		public OSCMessageEntry(OscMsgType type) {
			this.type = type;
		}

		public OSCMessageEntry(OscMsgType type, Object defaultValue) {
			this.type = type;
			this.defaultValue = Optional.of(getValue(defaultValue));
		}

		/*
		 * todo: exc when not castable?
		 */
		private Object getValue(String value) {
			switch (this.type) {
			case i:
				return Integer.valueOf(value);
			case f:
				return Float.valueOf(value);
			case s:
			default: // peacefull
				return value;
			}
		}

		/*
		 * todo: exc when not castable?
		 */
		private Object getValue(Object value) {
			switch (this.type) {
			case i:
				return (int) value;
			case f:
				return (float) value;
			case s:
			default: // this is not plausible, but method wont build otherwise:
						// (no return)
				return (String) value;
			}
		}

		public String getTypeString() {
			switch (this.type) {
			case i:
				return "i";
			case f:
				return "f";
			case s:
			default: // this is a very awkward moment for all of us
				return "s";
			}
		}

		public static enum OscMsgType {
			i, // int
			f, // float
			s // string
		}
	}
}
