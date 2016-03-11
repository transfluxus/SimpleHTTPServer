package http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class SHTTPSHandler implements HttpHandler {

	protected HttpExchange exchange;
	
	protected void setHttpExchange(HttpExchange exchange) {
		this.exchange = exchange;
	}
	
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
		return queryToMap(exchange.getRequestURI().getQuery());
	}
	
	/**
	 * returns the url parameters in a map
	 * 
	 * @param query
	 * @return map
	 */
	protected Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		if(query == null)
			return result;
		String[] params = query.split("&");
		for (String param : params) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}
	
}
