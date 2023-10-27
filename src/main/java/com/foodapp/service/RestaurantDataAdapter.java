package com.foodapp.service;

import com.foodapp.model.*;
import com.foodapp.model.enums.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantDataAdapter {

    private Connection connection;

    public RestaurantDataAdapter(Connection connection) {
        this.connection = connection;
    }

    public List<Restaurant> findRestaurantsByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from RESTAURANT where name like ?");
        preparedStatement.setString(1, "%" + name + "%");

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
            restaurant.setDesc(resultSet.getString(7));
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

    public List<Dish> getDishes(int restaurantId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from MENU where restaurant_id = ?");
        preparedStatement.setInt(1, restaurantId);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Dish> dishes = new ArrayList<>();
        while (resultSet.next()) {
            Dish dish = new Dish();
            dish.setName(resultSet.getString(1));
            dish.setDescription(resultSet.getString(2));
            dish.setPrice(resultSet.getDouble(3));
            dish.setRestaurantId(resultSet.getInt(4));
            dish.setDishId(resultSet.getInt(5));
            dishes.add(dish);
        }
        return dishes;
    }

    public String findDishImage(String dishId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select image from MENU where dish_id = ?");
        preparedStatement.setString(1, dishId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    public double getDishPrice(int dishId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select price from MENU where dish_id = ?");
        preparedStatement.setInt(1, dishId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble(1);
        }
        throw new RuntimeException("Unable to get dish price. Dish ID: " + dishId);
    }

    public Order saveOrder(Order order) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement saveStatement = connection.prepareStatement("insert into ORDERS (restaurant_id, customer_id, delivery_agent_id, address, status, total_cost) values (?,?,?,?,?,?)");
        saveStatement.setInt(1, order.getRestaurantId());
        saveStatement.setInt(2, order.getCustomerId());
        saveStatement.setObject(3, order.getDeliveryAgentId());
        saveStatement.setString(4, order.getAddress());
        saveStatement.setString(5, order.getOrderStatus().toString());
        saveStatement.setDouble(6, order.getTotalCost());
        int result = saveStatement.executeUpdate();

        if (result == 0) {
            connection.rollback();
            throw new RuntimeException("Unable to create order!");
        }

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select last_insert_rowid()");
        Integer orderId = null;
        if (resultSet.next()) {
            orderId = resultSet.getInt(1);
        }
        if (Objects.isNull(orderId)) {
            connection.rollback();
            throw new RuntimeException("Unable to create order!");
        }
        order.setOrderId(orderId);

        PreparedStatement saveOrderItemStatement = connection.prepareStatement("insert into ORDER_ITEMS (order_id, dish_id, qty) values (?, ?, ?)");

        for (OrderItem orderItem : order.getOrderItems()) {
            saveOrderItemStatement.setInt(1, orderId);
            saveOrderItemStatement.setInt(2, orderItem.getDishId());
            saveOrderItemStatement.setInt(3, orderItem.getQuantity());
            result = saveOrderItemStatement.executeUpdate();
            if (result == 0) {
                connection.rollback();
                throw new RuntimeException("Unable to create order!");
            }
        }
        connection.commit();
        connection.setAutoCommit(true);

        return order;
    }

    public void updateOrderAddress(int orderId, String address) throws SQLException {
        PreparedStatement updateStatement = connection.prepareStatement("update ORDERS set address=? where order_id = ?");
        updateStatement.setString(1, address);
        updateStatement.setInt(2, orderId);
        int result = updateStatement.executeUpdate();
        if (result == 0) throw new RuntimeException("Unable to update order address!");
    }

    public void updateOrderStatus(int orderId, OrderStatus status) throws SQLException {
        PreparedStatement updateStatement = connection.prepareStatement("update ORDERS set status=? where order_id = ?");
        updateStatement.setString(1, status.toString());
        updateStatement.setInt(2, orderId);
        int result = updateStatement.executeUpdate();
        if (result == 0) throw new RuntimeException("Unable to update order status!");
    }

    public void createDish(CreateDishRequest createDishRequest) throws SQLException {
        PreparedStatement createDishStatement = connection.prepareStatement("insert into MENU (dish, desc, price, restaurant_id, image) values (?, ?, ?, ?, ?)");
        createDishStatement.setString(1, createDishRequest.getDishName());
        createDishStatement.setString(2, createDishRequest.getDishDesc());
        createDishStatement.setDouble(3, createDishRequest.getDishPrice());
        createDishStatement.setInt(4, createDishRequest.getRestaurantId());
        createDishStatement.setString(5, createDishRequest.getImage());
        int result = createDishStatement.executeUpdate();
        if (result == 0) throw new RuntimeException("Unable to save dish!");
    }

    public List<Order> findOrdersByRestaurantId(int restaurantId) throws SQLException {
        PreparedStatement orderStatement = connection.prepareStatement("select * from ORDERS where restaurant_id = ?");
        orderStatement.setInt(1, restaurantId);

        ResultSet resultSet = orderStatement.executeQuery();

        List<Order> orders = new ArrayList<>();

        while (resultSet.next()) {
            Order order = new Order();
            order.setOrderId(resultSet.getInt(1));
            order.setRestaurantId(resultSet.getInt(2));
            order.setCustomerId(resultSet.getInt(3));
            order.setDeliveryAgentId(resultSet.getInt(4));
            order.setAddress(resultSet.getString(5));
            order.setOrderStatus(OrderStatus.valueOf(resultSet.getString(6)));
            order.setTotalCost(resultSet.getDouble(7));
            orders.add(order);
        }

        PreparedStatement orderItemStatement = connection.prepareStatement("select * from ORDER_ITEMS where order_id = ?");

        for (Order order: orders) {
            orderItemStatement.setInt(1, order.getOrderId());
            resultSet = orderItemStatement.executeQuery();
            List<OrderItem> items = new ArrayList<>();
            while (resultSet.next()) {
                OrderItem item = new OrderItem();
                item.setDishId(resultSet.getInt(2));
                item.setQuantity(resultSet.getInt(3));
                items.add(item);
            }
            order.setOrderItems(items);
        }
        return orders;
    }

    public List<Order> findReadyOrdersByStatus(OrderStatus orderStatus) throws SQLException {
        PreparedStatement orderStatement = connection.prepareStatement("select * from ORDERS where status = ?");
        orderStatement.setString(1, orderStatus.toString());

        ResultSet resultSet = orderStatement.executeQuery();

        List<Order> orders = new ArrayList<>();

        while (resultSet.next()) {
            Order order = new Order();
            order.setOrderId(resultSet.getInt(1));
            order.setRestaurantId(resultSet.getInt(2));
            order.setCustomerId(resultSet.getInt(3));
            order.setDeliveryAgentId(resultSet.getInt(4));
            order.setAddress(resultSet.getString(5));
            order.setOrderStatus(OrderStatus.valueOf(resultSet.getString(6)));
            order.setTotalCost(resultSet.getDouble(7));
            orders.add(order);
        }

        PreparedStatement orderItemStatement = connection.prepareStatement("select * from ORDER_ITEMS where order_id = ?");

        for (Order order: orders) {
            orderItemStatement.setInt(1, order.getOrderId());
            resultSet = orderItemStatement.executeQuery();
            List<OrderItem> items = new ArrayList<>();
            while (resultSet.next()) {
                OrderItem item = new OrderItem();
                item.setDishId(resultSet.getInt(2));
                item.setQuantity(resultSet.getInt(3));
                items.add(item);
            }
            order.setOrderItems(items);
        }
        return orders;
    }

    public List<Restaurant> findRestaurantsByCuisine(String cuisine) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from RESTAURANT where cuisine like ?");
        preparedStatement.setString(1, "%" + cuisine + "%");

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
            restaurant.setDesc(resultSet.getString(7));
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    public List<Restaurant> findRestaurantsByRating(int rating) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from RESTAURANT where rating = ?");
        preparedStatement.setInt(1, rating);

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
            restaurant.setDesc(resultSet.getString(7));
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    public Dish findDishDetails(int dishId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from MENU where dish_id = ?");
        preparedStatement.setInt(1, dishId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Dish dish = new Dish();
            dish.setName(resultSet.getString(1));
            dish.setDescription(resultSet.getString(2));
            dish.setPrice(resultSet.getDouble(3));
            dish.setRestaurantId(resultSet.getInt(4));
            dish.setDishId(resultSet.getInt(5));
            return dish;
        }
        return null;
    }

    public Restaurant findRestaurantById(int restaurantId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from RESTAURANT where restaurant_id = ?");
        preparedStatement.setInt(1, restaurantId);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(resultSet.getString(1));
            restaurant.setAddress(resultSet.getString(2));
            restaurant.setRestaurantId(resultSet.getInt(3));
            restaurant.setRating(resultSet.getInt(4));
            restaurant.setCuisine(resultSet.getString(5));
//            restaurant.setImage(resultSet.getString(6));
            restaurant.setDesc(resultSet.getString(7));
            return restaurant;
        }

        return null;
    }
}
