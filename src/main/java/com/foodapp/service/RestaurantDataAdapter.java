package com.foodapp.service;

import com.foodapp.model.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDataAdapter {

    private Connection connection;

    public RestaurantDataAdapter(Connection connection) {
        this.connection = connection;
    }

    public List<Restaurant> findRestaurants(String name, String cuisine, Integer rating) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from RESTAURANT where name like ? and cuisine like ? and rating = ?");
        preparedStatement.setString(1, "%" + name + "%");
        preparedStatement.setString(2, "%" + cuisine + "%");
        preparedStatement.setInt(3, rating);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Restaurant> restaurants = new ArrayList<>();

        while (resultSet.next()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(resultSet.getString(1));
            restaurant.setAddress(resultSet.getString(2));
            restaurant.setRestaurantId(resultSet.getInt(3));
            restaurant.setRating(resultSet.getInt(4));
            restaurant.setCuisine(resultSet.getString(5));
//            restaurant.setImage(resultSet.getString(6));
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    public String findRestaurantImage(String restaurantId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select image from RESTAURANT where restaurant_id = ?");
        preparedStatement.setString(1, restaurantId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }
}
