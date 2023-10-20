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
import com.foodapp.service.LoginService;
import com.foodapp.service.RestaurantService;

import java.sql.SQLException;
import java.util.List;

public class RestaurantController extends Controller {

    private RestaurantService restaurantService;
    private LoginService loginService;

    public RestaurantController(RestaurantService restaurantService, LoginService loginService) {
        this.restaurantService = restaurantService;
        this.loginService = loginService;
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

        List<Dish> dishes = restaurantService.getDishes(restaurantId);

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

}
