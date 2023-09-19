package com.foodapp;

import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.controller.ControllerProcessor;
import com.foodapp.framework.webserver.WebServer;

import java.io.IOException;
import java.util.logging.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws IOException {
        Integer port = null;
        if (args.length > 0)
             port = Integer.parseInt(args[0]);
        WebServer webServer = new WebServer(port);
        Controller controller = new Controller();
        ControllerProcessor controllerProcessor = new ControllerProcessor(webServer);
        controllerProcessor.process(controller);
        webServer.start();
        logger.info("Started application on port: " + webServer.getPort());
    }

}
