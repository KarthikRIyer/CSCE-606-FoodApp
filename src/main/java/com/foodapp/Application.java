package com.foodapp;

import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.controller.ControllerProcessor;
import com.foodapp.framework.webserver.WebServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws IOException {
        Map<String, String> argsKeyVal = parseArgs(args);
        Integer port = Optional.ofNullable(argsKeyVal.get("port")).map(Integer::parseInt).orElse(null);
        WebServer webServer = new WebServer(port);
        Controller controller = new Controller();
        ControllerProcessor controllerProcessor = new ControllerProcessor(webServer);
        controllerProcessor.process(controller);
        webServer.start();
        logger.info("Started application on port: " + webServer.getPort());
    }

    private static Map<String, String> parseArgs(String[] args) {
        if (args.length % 2 != 0)
            throw new RuntimeException("Unable to pass args. All args should be key value pairs");
        Map<String, String> argKeyVal = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            String key = args[i];
            if (!key.startsWith("--"))
                throw new RuntimeException("Unable to recognize argument: " + key + "\nArgument key should start with --\nEg: --port");
            key = key.replace("--", "");
            String val = args[i+1];
            argKeyVal.put(key, val);
        }
        return argKeyVal;
    }

}
