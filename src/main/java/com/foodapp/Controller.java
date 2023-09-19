package com.foodapp;

import com.foodapp.annotation.GET;
import com.foodapp.annotation.RequestParam;
import com.foodapp.util.HttpResponse;

public class Controller {

    public Controller() {}

    @GET(path = "/hello")
    public HttpResponse hello(@RequestParam("name") String name) {
        return new HttpResponse("Hello " + name + "!", 200);
    }

}
