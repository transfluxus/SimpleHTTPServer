package http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * With these handlers you can go wild.
 * Common use is to provide webservices.
 * 
 * @author raminsoleymani
 *
 */
public class DynamicResponseHandler extends SHTTPSHandler {

	private ResponseBuilder responseBuilder;
	private String contentType;
	


	public DynamicResponseHandler(ResponseBuilder responseBuilder, String contentType) {
		this.responseBuilder = responseBuilder;
		this.responseBuilder.parent = this;
		this.contentType = contentType;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			setHttpExchange(exchange);
			String requestBody = parseRequestBody(exchange);
			Headers headers = exchange.getResponseHeaders();
			headers.add("Content-Type", contentType);
			byte[] response = responseBuilder.responseBytes(requestBody);
			exchange.sendResponseHeaders(200, response.length);
			OutputStream os = exchange.getResponseBody();
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
	
	@Override
	public String toString() {
		return "DynamicResponseHandler: "+contentType;
	}
}
