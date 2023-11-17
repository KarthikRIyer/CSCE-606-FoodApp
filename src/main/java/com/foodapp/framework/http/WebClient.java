package com.foodapp.framework.http;

import com.foodapp.framework.registry.RegistryClient;
import com.foodapp.framework.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class WebClient {

    private RegistryClient registryClient;

    public WebClient(RegistryClient registryClient) {
        this.registryClient = registryClient;
    }

    public HttpResponse<String> post(String url, String body) throws URISyntaxException, IOException, InterruptedException {
        URI targetURI = new URI(url);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(targetURI).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> postLoadBalanced(String url, String body) throws URISyntaxException, IOException, InterruptedException {
        String serviceName = url.substring(7, url.substring(7).indexOf('/') + 7);
        String serviceURL = registryClient.getServiceURL(serviceName);
        if (Objects.isNull(serviceURL)) {
            throw new RuntimeException("Unable to resolve service url for " + serviceName);
        }
        url = url.replace(serviceName, serviceURL);
        URI targetURI = new URI(url);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(targetURI).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

}
