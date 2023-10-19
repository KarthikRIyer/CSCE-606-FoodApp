package com.foodapp.framework.controller;

import com.foodapp.framework.annotation.GET;
import com.foodapp.framework.annotation.POST;
import com.foodapp.framework.annotation.RequestBody;
import com.foodapp.framework.annotation.RequestParam;
import com.foodapp.framework.util.Constants;
import com.foodapp.framework.util.HttpResponse;
import com.foodapp.framework.webserver.WebServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

public class ControllerProcessor {

    WebServer webServer;
    Logger logger = Logger.getLogger(ControllerProcessor.class.getName());
    public ControllerProcessor(WebServer webServer) {
        this.webServer = webServer;
    }

    public void process(Controller controller) {
        Class<? extends Controller> clazz = controller.getClass();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(GET.class)) {
                GET getAnnotation = m.getAnnotation(GET.class);
                String path = getAnnotation.path();
                webServer.createContext(path, exchange -> {
                    if (!Constants.GET.equals(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(405, -1);
                        exchange.close();
                        return;
                    }

                    HttpResponse response = null;

                    Object[] paramValues = new Object[0];
                    try {
                        paramValues = parseRequestParamsAndBody(exchange, m, Constants.GET);
                    } catch (Exception e) {
                        handleException(e, exchange);
                    }

                    try {
                        response = (HttpResponse) m.invoke(controller, paramValues);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleException(e, exchange);
                    }

                    assert response != null;
                    logger.info("Serving " + Constants.GET + " request: " + exchange.getRequestURI().getRawPath() + "?" + exchange.getRequestURI().getRawQuery());
                    handleResponse(response, exchange, Constants.APPLICATION_JSON);
                });
            } else if (m.isAnnotationPresent(POST.class)) {
                POST postAnnotation = m.getAnnotation(POST.class);
                String path = postAnnotation.path();
                webServer.createContext(path, exchange -> {
                    if (!Constants.POST.equals(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(405, -1);
                        exchange.close();
                        return;
                    }

                    HttpResponse response = null;

                    Object[] paramValues = new Object[0];
                    try {
                        paramValues = parseRequestParamsAndBody(exchange, m, Constants.POST);
                    } catch (Exception e) {
                        handleException(e, exchange);
                    }

                    try {
                        response = (HttpResponse) m.invoke(controller, paramValues);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleException(e, exchange);
                    }

                    assert response != null;
                    logger.info("Serving " + Constants.POST + " request: " + exchange.getRequestURI().getRawPath() + "?" + exchange.getRequestURI().getRawQuery());
                    handleResponse(response, exchange, Constants.APPLICATION_JSON);
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

    private Object[] parseRequestParamsAndBody(HttpExchange exchange, Method m, String requestType) throws IOException {
        Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
        String requestBodyStr = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        int paramCount = m.getParameterCount();
        Object[] finalParams = new Object[paramCount];
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        for (int i = 0; i < paramCount; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            RequestParam requestParam = (RequestParam) Arrays.stream(annotations).filter(a -> a instanceof RequestParam).findFirst().orElse(null);
            if (!Objects.isNull(requestParam)) {
                String paramName = requestParam.value();
                String value = params.getOrDefault(paramName, Collections.emptyList()).stream().findFirst().orElse(null);
                if (Objects.isNull(value) && requestParam.required()) {
                    throw new RuntimeException("Request does not contain required parameter: " + paramName);
                }
                finalParams[i] = value;
            }

            RequestBody requestBody = (RequestBody) Arrays.stream(annotations).filter(a -> a instanceof RequestBody).findFirst().orElse(null);
            if (!Objects.isNull(requestBody) && Constants.GET.equals(requestType)) {
                throw new RuntimeException("RequestBody not allowed for GET request!");
            } else if (!Objects.isNull(requestBody)) {
                finalParams[i] = requestBodyStr;
            }
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

    private void handleResponse(HttpResponse response, HttpExchange exchange, String contentType) throws IOException {
        exchange.getResponseHeaders()
                .set(Constants.CONTENT_TYPE, Optional.ofNullable(contentType)
                        .orElse(Constants.APPLICATION_JSON));
        exchange.sendResponseHeaders(response.responseCode, response.responseText.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.responseText.getBytes());
        outputStream.flush();
        exchange.close();
    }
}
