package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

abstract class SHTTPSHandler implements HttpHandler {

	protected HttpExchange exchange;
	
	protected void setHttpExchange(HttpExchange exchange) {
		this.exchange = exchange;
	}
	
	/**
	 * get the query parameters of the recent exchange as map of fields as keys and values
	 * e.g. something like ...?user=Pris&age=2 would turn into a map
	 * "user" : "Pris", "age" : "2" 
	 * @return map containing all queryparameters
	 */
	public Map<String, String> queryToMap() {
		return queryToMap(exchange.getRequestURI().getQuery());
	}

	/**
	 * get the query parameters of given HttpExchange as map of fields as keys and values
	 * e.g. something like ...?user=Pris&age=2 would turn into a map
	 * "user" : "Pris", "age" : "2" 
	 * 
	 * @param exchange that contains the URI to take the parameters from
	 * @return map containing all queryparameters
	 */
//	protected Map<String, String> queryToMap(HttpExchange exchange) {
//		return queryToMap(exchange);
//	}
	
	/**
	 * get the query parameters of given HttpExchange as map of fields as keys and values
	 * e.g. something like ...?user=Pris&age=2 would turn into a map
	 * "user" : "Pris", "age" : "2" 
	 * 
	 * @param querystring from the URI
	 * @return map containing all queryparameters
	 */
	protected Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
//		System.out.println(result.toString());
		InputStreamReader br = new InputStreamReader(exchange.getRequestBody());
		try {
			System.out.println("reading");
			while(br.ready()){
				System.out.print((char)br.read());
			}
			System.out.println("body");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public HttpExchange getExchange(){
		return exchange;
	}
}
