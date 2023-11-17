package com.foodapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodapp.framework.http.WebClient;
import com.foodapp.framework.util.JsonUtil;
import com.foodapp.model.PaymentRequest;
import com.foodapp.model.TxnDetails;
import com.foodapp.model.TxnResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentService {

    private String paymentServiceURL = "http://PAYMENT_SERVICE";
    private RestaurantDataAdapter restaurantDataAdapter;
    private WebClient webClient;

    public PaymentService(RestaurantDataAdapter restaurantDataAdapter, WebClient webClient) {
        this.restaurantDataAdapter = restaurantDataAdapter;
        this.webClient = webClient;
    }

    public TxnResponse processPayment(PaymentRequest paymentRequest) throws SQLException, URISyntaxException, IOException, InterruptedException {
        restaurantDataAdapter.updateOrderPhoneNo(paymentRequest.getOrderId(),paymentRequest.getPhoneNumber());

        int restaurantId = restaurantDataAdapter.getRestaurantId(paymentRequest.getOrderId());
        TxnDetails txnDetails = new TxnDetails();
        txnDetails.setCardNo(paymentRequest.getCardNumber());
        txnDetails.setRestaurantId(restaurantId);
        txnDetails.setAmount(paymentRequest.getAmount());
        txnDetails.setOrderId(paymentRequest.getOrderId());

        String url = paymentServiceURL + "/postTxn";
        HttpResponse<String> response = webClient.postLoadBalanced(url, JsonUtil.toJson(txnDetails));
//        URI targetURI = new URI(url);
//        HttpRequest httpRequest = HttpRequest.newBuilder().uri(targetURI).POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(txnDetails))).build();
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        TxnResponse txnResponse = JsonUtil.fromJson(response.body(), TxnResponse.class);
        return txnResponse;
    }

}
