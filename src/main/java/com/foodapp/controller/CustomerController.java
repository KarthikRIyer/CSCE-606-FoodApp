package com.foodapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodapp.framework.annotation.GET;
import com.foodapp.framework.annotation.RequestParam;
import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.util.HttpResponse;
import com.foodapp.framework.util.JsonUtil;
import com.foodapp.model.Order;
import com.foodapp.service.CustomerService;
import com.foodapp.service.LoginService;

import java.sql.SQLException;
import java.util.List;

public class CustomerController extends Controller {

    private CustomerService customerService;
    private LoginService loginService;

    public CustomerController(CustomerService customerService, LoginService loginService) {
        this.customerService = customerService;
        this.loginService = loginService;
    }

    @GET(path = "/customerOrders")
    public HttpResponse customerOrders(@RequestParam("userId") String userId,
                                       @RequestParam("token") String token) throws JsonProcessingException, SQLException {
        loginService.validateToken(userId, token);

        List<Order> orders = customerService.findOrders(Integer.parseInt(userId));

        return new HttpResponse(JsonUtil.toJson(orders), 200);
    }

}
