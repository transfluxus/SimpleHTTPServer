import http.*;
import http.prefab.*;
import http.prefab.guiElement.*;
import http.prefab.test.*;

SimpleHTTPServer server;

void setup() {
    server = new SimpleHTTPServer(this);
    server.serveAll("");
}