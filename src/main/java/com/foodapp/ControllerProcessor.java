package com.foodapp;

import com.foodapp.annotation.GET;
import com.foodapp.annotation.RequestParam;
import com.foodapp.util.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

public class ControllerProcessor {

    WebServer webServer;

    public ControllerProcessor(WebServer webServer) {
        this.webServer = webServer;
    }

    public void process(Controller controller) {
        Class<? extends Controller> clazz = controller.getClass();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(GET.class)) {
                GET getAnnotation = m.getAnnotation(GET.class);
                String path = getAnnotation.path();
                webServer.createContext(path, new HttpHandler() {
                    @Override
                    public void handle(HttpExchange exchange) throws IOException {
                        if (!"GET".equals(exchange.getRequestMethod())) {
                            exchange.sendResponseHeaders(405, -1);
                            exchange.close();
                            return;
                        }

                        HttpResponse response = null;

                        Object[] paramValues = new Object[0];
                        try {
                            paramValues = parseRequestParams(exchange, m);
                        } catch (Exception e) {
                            handleException(e, exchange);
                        }

                        try {
                            response = (HttpResponse) m.invoke(controller, paramValues);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            handleException(e, exchange);
                        }

                        assert response != null;
                        handleResponse(response, exchange);
                    }
                });
            }
        }

    }

    private static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode(final String encoded) {
        return encoded == null ? null : URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    private Object[] parseRequestParams(HttpExchange exchange, Method m) {
        Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
        int paramCount = m.getParameterCount();
        Object[] finalParams = new Object[paramCount];
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        for (int i = 0; i < paramCount; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            RequestParam requestParam = (RequestParam) Arrays.stream(annotations).filter(a -> a instanceof RequestParam).findFirst().orElse(null);
            if (requestParam != null) {
                String paramName = requestParam.value();
                String value = params.getOrDefault(paramName, Collections.emptyList()).stream().findFirst().orElse(null);
                if (Objects.isNull(value) && requestParam.required()) {
                    throw new RuntimeException("Request does not contain required parameter: " + paramName);
                }
                finalParams[i] = value;
            };
        }
        return finalParams;
    }

    private void handleException(Exception e, HttpExchange exchange) throws IOException {
        e.printStackTrace();
        HttpResponse response = new HttpResponse(e.getMessage(), 500);
        exchange.sendResponseHeaders(response.responseCode, response.responseText.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.responseText.getBytes());
        outputStream.flush();
        exchange.close();
    }

    private void handleResponse(HttpResponse response, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(response.responseCode, response.responseText.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.responseText.getBytes());
        outputStream.flush();
        exchange.close();
    }
}
