package com.foodapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodapp.framework.annotation.GET;
import com.foodapp.framework.annotation.RequestParam;
import com.foodapp.framework.controller.Controller;
import com.foodapp.framework.util.HttpResponse;
import com.foodapp.framework.util.JsonUtil;
import com.foodapp.model.User;
import com.foodapp.service.LoginService;

import java.sql.SQLException;

public class LoginController extends Controller {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GET(path = "/login")
    public HttpResponse login(@RequestParam("username") String username,
                              @RequestParam("password") String password) throws JsonProcessingException, SQLException {
        User user = loginService.loginAndGenerateToken(username, password);
        return new HttpResponse(JsonUtil.toJson(user), 200);
    }

}
