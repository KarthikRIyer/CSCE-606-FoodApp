package com.foodapp.model;

public class OrderItem {
    private int dishId;
    private int quantity;
    public int getQuantity() {
        return quantity;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
