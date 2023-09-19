package com.foodapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebServer {

    HttpServer httpServer;
    int port = 8000;

    public WebServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null);
    }


    public void createContext(String path, HttpHandler handler) {
        httpServer.createContext(path, handler);
    }


    public void start() {
        httpServer.start();
    }

}
