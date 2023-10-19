package com.foodapp;

import com.foodapp.controller.LoginController;
import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.controller.ControllerProcessor;
import com.foodapp.framework.webserver.WebServer;
import com.foodapp.service.LoginDataAdapter;
import com.foodapp.service.LoginService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());

    private static Application instance;

    private Connection connection;
    private WebServer webServer;
    private ControllerProcessor controllerProcessor;
    private String url = "jdbc:sqlite:FoodApp.db";

    private LoginController loginController;
    private LoginDataAdapter loginDataAdapter;
    private LoginService loginService;

    public static void initApp(String[] args) throws SQLException, IOException {
        if (Objects.isNull(instance)) {
            instance = new Application(args);
        }
    }

    public static Application getInstance() {
        return instance;
    }

    private Application(String[] args) throws IOException, SQLException {
        Map<String, String> argsKeyVal = parseArgs(args);
        Integer port = Optional.ofNullable(argsKeyVal.get("port")).map(Integer::parseInt).orElse(null);
        webServer = new WebServer(port);

        connection = DriverManager.getConnection(url);
        loginDataAdapter = new LoginDataAdapter(connection);
        loginService = new LoginService(loginDataAdapter);

        controllerProcessor = new ControllerProcessor(webServer);

        loginController = new LoginController(loginService);
        controllerProcessor.process(loginController);
    }

    public void init() {
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
