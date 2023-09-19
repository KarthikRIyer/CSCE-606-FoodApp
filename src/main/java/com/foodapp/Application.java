package com.foodapp;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer();
        Controller controller = new Controller();
        ControllerProcessor controllerProcessor = new ControllerProcessor(webServer);
        controllerProcessor.process(controller);
        webServer.start();
    }

}
