package com.foodapp.service;

import com.foodapp.model.Restaurant;

import java.sql.SQLException;
import java.util.List;

public class RestaurantService {

    private RestaurantDataAdapter restaurantDataAdapter;

    public RestaurantService(RestaurantDataAdapter restaurantDataAdapter) {
        this.restaurantDataAdapter = restaurantDataAdapter;
    }

    public List<Restaurant> findRestaurants(String name, String cuisine, Integer rating) throws SQLException {
        return restaurantDataAdapter.findRestaurants(name, cuisine, rating);
    }
}
