package http;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class ExtHttpHandler implements HttpHandler {

	protected HttpExchange exchange;

	public Map<String, String> queryToMap() {
		return queryToMap(exchange);
	}

	/**
	 * returns the url parameters in a map
	 * 
	 * @param query
	 * @return map
	 */
	protected Map<String, String> queryToMap(HttpExchange exchange) {
		// String uri = exchange.getRequestURI().toString();
		String query = exchange.getRequestURI().getQuery();
		// PApplet.println("query:",query);
		Map<String, String> map = queryToMap(query);
		return map;
	}

	/**
	 * returns the url parameters in a map
	 * 
	 * @param query
	 * @return map
	 */
	protected Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}

	protected void setHttpExchange(HttpExchange exchange) {
		this.exchange = exchange;
	}
}
