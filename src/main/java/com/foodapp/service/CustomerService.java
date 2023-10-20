package com.foodapp.service;

import com.foodapp.model.Order;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private CustomerDataAdapter customerDataAdapter;

    public CustomerService(CustomerDataAdapter customerDataAdapter) {
        this.customerDataAdapter = customerDataAdapter;
    }

    public List<Order> findOrders(int customerId) throws SQLException {
        return customerDataAdapter.findOrdersByCustomerId(customerId);
    }
}
