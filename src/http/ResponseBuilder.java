package http;

import java.util.Map;

/**
 * Use these abstract class to build Webservices.
 * @author raminsoleymani
 *
 */
public abstract class ResponseBuilder {
	
	protected DynamicResponseHandler parent;
	
	
	/**
	 * Override this function to respond
	 * @param requestString request
	 * @return response
	 */
	public abstract String getResponse(String requestString);

	protected Map<String,String> getQueryMap() {
		return parent.queryToMap();
	}

	public byte[] responseBytes(String requestString) {
		return getResponse(requestString).getBytes();
	}
	
}
