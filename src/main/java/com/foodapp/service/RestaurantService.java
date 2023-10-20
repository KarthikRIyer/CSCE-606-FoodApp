package com.foodapp.service;

import com.foodapp.model.*;
import com.foodapp.model.enums.OrderStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public List<Dish> getDishes(int restaurantId) throws SQLException {
        List<Dish> dishes = restaurantDataAdapter.getDishes(restaurantId);
        return dishes;
    }

    public String getDishImage(String dishId) throws SQLException {
        String img = restaurantDataAdapter.findDishImage(dishId);
        if (Objects.isNull(img)) {
            throw new RuntimeException("Unable to find image!");
        }
        return img;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest, int userId) throws SQLException {
        Order order = new Order();
        List<OrderItem> orderItems = createOrderRequest.getDishes().stream()
                .map(i -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setQuantity(i.getQuantity());
                    orderItem.setDishId(i.getDishId());
                    return orderItem;
                }).collect(Collectors.toList());
        order.setCustomerId(userId);
        order.setOrderItems(orderItems);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setRestaurantId(createOrderRequest.getRestaurantId());

        double totalCost = orderItems.stream().mapToDouble(oi -> {
            try {
                return restaurantDataAdapter.getDishPrice(oi.getDishId()) * oi.getQuantity();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).sum();
        order.setTotalCost(totalCost);

        order = restaurantDataAdapter.saveOrder(order);

        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderId(order.getOrderId());
        createOrderResponse.setTotalCost(totalCost);

        return createOrderResponse;
    }

    public void updateOrderAddress(int orderId, String address) throws SQLException {
        restaurantDataAdapter.updateOrderAddress(orderId, address);
    }

    public void updateOrderStatus(int orderId, OrderStatus confirmed) throws SQLException {
        restaurantDataAdapter.updateOrderStatus(orderId, confirmed);
    }
}