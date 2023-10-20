package com.foodapp.service;

import com.foodapp.model.Order;
import com.foodapp.model.OrderItem;
import com.foodapp.model.enums.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataAdapter {

    private Connection connection;

    public CustomerDataAdapter(Connection connection) {
        this.connection = connection;
    }

    public List<Order> findOrdersByCustomerId(int customerId) throws SQLException {

        PreparedStatement orderStatement = connection.prepareStatement("select * from ORDERS where customer_id = ?");
        orderStatement.setInt(1, customerId);

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
}
