package http;

import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP_Testing {

	public static boolean  testFor200(String address) throws Exception{
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		int code = conn.getResponseCode();
		return code == 200;
	}
	
	public static void testCallback() throws Exception {
		testFor200("http://localhost:8000/bg.html?r=255&g=128&b=0");
		testFor200("http://127.0.0.1:8000/bg.html?b=200");
		testFor200("http://localhost:8000/bg?b=230&g=200");
	}

	public static void testEchoJSON() throws Exception {
		testFor200("http://localhost:8000");
	}
	
	public static void testExternalCss() throws Exception {
		testFor200("http://localhost:8000");
	}
	
	public static void testFileTemplate1() throws Exception {
		testFor200("http://localhost:8000");
		testFor200("http://localhost:8000?user=ramin");
	}
	
	public static void main(String[] args) throws Exception {
		testCallback();
		testEchoJSON();
		testExternalCss();
		testFileTemplate1();
	}
}