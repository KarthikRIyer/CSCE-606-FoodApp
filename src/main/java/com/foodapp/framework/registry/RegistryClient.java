package com.foodapp.framework.registry;

import com.foodapp.framework.util.JsonUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class RegistryClient {
    private String registryURL;
    private String serviceName;
    private String hostAddress;
    private Random random;
    private AtomicReference<Map<String, List<RegistryElement>>> registryMap;

    private final ScheduledExecutorService scheduler;
    private final Logger logger = Logger.getLogger(RegistryClient.class.getName());

    public RegistryClient(String registryURL, String serviceName, String hostAddress) {
        this.registryURL = registryURL;
        this.serviceName = serviceName;
        this.hostAddress = hostAddress.replace("/", "");
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.registryMap = new AtomicReference<>(new ConcurrentHashMap<>());
        this.scheduler.scheduleAtFixedRate(this::registerService, 0, 1, TimeUnit.MINUTES);
        this.scheduler.scheduleAtFixedRate(this::updateRegistry, 15, 30, TimeUnit.SECONDS);
    }

    private void registerService() {
        try {
            logger.info("Sending heartbeat to service registry");
            String url = registryURL + "/registerServiceUrl?serviceName="+serviceName+"&url="+hostAddress;
            URI targetURI = new URI(url);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(targetURI).POST(HttpRequest.BodyPublishers.noBody()).build();
            HttpClient httpClient = HttpClient.newHttpClient();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRegistry() {
        try {
            String url = registryURL + "/getRegistryJSON";
            URI targetURI = new URI(url);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(targetURI).GET().build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            json = json.replace("\\n", "");
            json = json.replace("\\r", "");
            json = json.replace("\\\"", "\"");
            registryMap.set(JsonUtil.registryMapfromJson(json.substring(1, json.length()-1)));
        } catch (Exception e) {
            logger.info("Unable to update registry cache");
        }
    }

    public String getServiceURL(String serviceName) {
        List<RegistryElement> urls = registryMap.get().get(serviceName);
        if (Objects.isNull(urls) || urls.isEmpty()) {
            updateRegistry();
            urls = registryMap.get().get(serviceName);
            if (Objects.isNull(urls) || urls.isEmpty()) {
                return null;
            }
        };
        return urls.get(random.nextInt(urls.size())).url;
    }

    public static class RegistryElement {
        private String url;
        private LocalDateTime dateTime;

        public RegistryElement() {}

        public RegistryElement(String url, LocalDateTime dateTime) {
            this.url = url;
            this.dateTime = dateTime;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
