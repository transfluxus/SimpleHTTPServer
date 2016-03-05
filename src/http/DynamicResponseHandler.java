package http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class DynamicResponseHandler extends ExtHttpHandler {

	private ResponseBuilder responseBuilder;
	private String contentType;

	public DynamicResponseHandler(ResponseBuilder responseBuilder, String contentType) {
		this.responseBuilder = responseBuilder;
		this.responseBuilder.parent = this;
		this.contentType = contentType;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		try {
			String requestBody = parseRequestBody(t);
			Headers headers = t.getResponseHeaders();
			headers.add("Content-Type", contentType);
			setHttpExchange(t);
			byte[] response = responseBuilder.responseBytes(requestBody);
			t.sendResponseHeaders(200, response.length);
			OutputStream os = t.getResponseBody();
			os.write(response);
			os.close();
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		}
	}

	private String parseRequestBody(HttpExchange t) throws IOException {
		InputStreamReader reader = new InputStreamReader(t.getRequestBody());
		StringBuilder sb = new StringBuilder();
		while (reader.ready())
			sb.append((char) reader.read());
		reader.close();
		// println(sb.toString());
		return sb.toString();
	}
}
