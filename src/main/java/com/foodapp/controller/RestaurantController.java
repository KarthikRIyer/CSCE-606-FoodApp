package com.foodapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodapp.framework.annotation.GET;
import com.foodapp.framework.annotation.POST;
import com.foodapp.framework.annotation.RequestBody;
import com.foodapp.framework.annotation.RequestParam;
import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.util.HttpResponse;
import com.foodapp.framework.util.JsonUtil;
import com.foodapp.model.*;
import com.foodapp.model.enums.OrderStatus;
import com.foodapp.service.LoginService;
import com.foodapp.service.PaymentService;
import com.foodapp.service.RestaurantService;

import java.sql.SQLException;
import java.util.List;

public class RestaurantController extends Controller {

    private RestaurantService restaurantService;
    private LoginService loginService;
    private PaymentService paymentService;

    public RestaurantController(RestaurantService restaurantService, LoginService loginService, PaymentService paymentService) {
        this.restaurantService = restaurantService;
        this.loginService = loginService;
        this.paymentService = paymentService;
    }

    @GET(path = "/searchRestaurant")
    public HttpResponse searchRestaurant(@RequestParam("name") String name,
                              @RequestParam("cuisine") String cuisine,
                              @RequestParam("rating") String rating,
                              @RequestParam("userId") String userId,
                              @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        List<Restaurant> restaurants = restaurantService.findRestaurants(name, cuisine, Integer.parseInt(rating));

        return new HttpResponse(JsonUtil.toJson(restaurants), 200);
    }

    @GET(path = "/restaurantImage")
    public HttpResponse restaurantImage(@RequestParam("restaurantId") String restaurantId,
                              @RequestParam("userId") String userId,
                              @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        String restaurantImg = restaurantService.getRestaurantImage(restaurantId);

        return new HttpResponse(restaurantImg, 200);
    }

    @GET(path = "/dishes")
    public HttpResponse getDishes(@RequestParam("restaurantId") String restaurantId,
                                        @RequestParam("userId") String userId,
                                        @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        List<Dish> dishes = restaurantService.getDishes(Integer.parseInt(restaurantId));

        return new HttpResponse(JsonUtil.toJson(dishes), 200);
    }

    @GET(path = "/dishImage")
    public HttpResponse dishImage(@RequestParam("dishId") String dishId,
                                        @RequestParam("userId") String userId,
                                        @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        String restaurantImg = restaurantService.getDishImage(dishId);

        return new HttpResponse(restaurantImg, 200);
    }

    @POST(path = "/createOrder")
    public HttpResponse createOrder(@RequestBody String createOrderRequestStr,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        CreateOrderRequest createOrderRequest = JsonUtil.fromJson(createOrderRequestStr, CreateOrderRequest.class);

        CreateOrderResponse createOrderResponse = restaurantService.createOrder(createOrderRequest, Integer.parseInt(userId));

        return new HttpResponse(JsonUtil.toJson(createOrderResponse), 200);
    }

    @POST(path = "/payOrder")
    public HttpResponse payOrder(@RequestBody String paymentRequestStr,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        PaymentRequest paymentRequest = JsonUtil.fromJson(paymentRequestStr, PaymentRequest.class);
        boolean processed = paymentService.processPayment(paymentRequest);

        if (!processed) {
            throw new RuntimeException("Could not process payment");
        }

        restaurantService.updateOrderAddress(paymentRequest.getOrderId(), paymentRequest.getAddress());
        restaurantService.updateOrderStatus(paymentRequest.getOrderId(), OrderStatus.CONFIRMED);

        return new HttpResponse("Payment processed. Order confirmed!", 200);
    }

    @POST(path = "/createDish")
    public HttpResponse createDish(@RequestBody String createDishRequestStr,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        CreateDishRequest createDishRequest = JsonUtil.fromJson(createDishRequestStr, CreateDishRequest.class);
        restaurantService.createDish(createDishRequest);

        return new HttpResponse("Dish created", 200);
    }

    @GET(path = "/restaurantOrders")
    public HttpResponse restaurantOrders(@RequestParam("restaurantId") String restaurantId,
                                         @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(restaurantId, token);
        List<Order> orders = restaurantService.findRestaurantOrders(Integer.parseInt(restaurantId));

        return new HttpResponse(JsonUtil.toJson(orders), 200);
    }

    @POST(path = "/orderPrepared")
    public HttpResponse orderPrepared(@RequestParam("restaurantId") String orderId,
                                      @RequestParam("restaurantId") String restaurantId,
                                      @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(restaurantId, token);
        restaurantService.orderPrepared(Integer.parseInt(orderId));
        return new HttpResponse("Order reade", 200);
    }

    @GET(path = "/readyOrders")
    public HttpResponse readyOrders(@RequestParam("userId") String userId,
                                         @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        List<Order> orders = restaurantService.findReadyOrders();

        return new HttpResponse(JsonUtil.toJson(orders), 200);
    }

}
