package com.foodapp.framework.controller;

import com.foodapp.framework.annotation.GET;
import com.foodapp.framework.annotation.POST;
import com.foodapp.framework.annotation.RequestBody;
import com.foodapp.framework.annotation.RequestParam;
import com.foodapp.framework.util.HttpResponse;

public class Controller {

    public Controller() {}

    @GET(path = "/hello")
    public HttpResponse hello(@RequestParam("name") String name) {
        return new HttpResponse("Hello " + name + "!", 200);
    }

    @POST(path = "/hello2")
    public HttpResponse hello2(@RequestParam("name") String name, @RequestBody String body) {
        return new HttpResponse("Hello " + name + "!" + "\nBody: " + body, 200);
    }

}
