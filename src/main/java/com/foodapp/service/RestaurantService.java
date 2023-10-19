package com.foodapp.service;

import com.foodapp.model.Dish;
import com.foodapp.model.Restaurant;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class RestaurantService {

    private RestaurantDataAdapter restaurantDataAdapter;

    public RestaurantService(RestaurantDataAdapter restaurantDataAdapter) {
        this.restaurantDataAdapter = restaurantDataAdapter;
    }

    public List<Restaurant> findRestaurants(String name, String cuisine, Integer rating) throws SQLException {
        return restaurantDataAdapter.findRestaurants(name, cuisine, rating);
    }

    public String getRestaurantImage(String restaurantId) throws SQLException {
        String img = restaurantDataAdapter.findRestaurantImage(restaurantId);
        if (Objects.isNull(img)) {
            throw new RuntimeException("Unable to find image!");
        }
        return img;
    }

    public List<Dish> getDishes(String restaurantId) throws SQLException {
        List<Dish> dishes = restaurantDataAdapter.getDishes(restaurantId);
        return dishes;
    }
}
