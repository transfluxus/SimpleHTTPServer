package http;

public abstract class ResponseBuilder {

	/**
	 * Override this function to respond
	 * @param requestString request
	 * @return response
	 */
	public abstract String getResponse(String requestString);

	public byte[] responseBytes(String requestString) {
		return getResponse(requestString).getBytes();
	}
}
