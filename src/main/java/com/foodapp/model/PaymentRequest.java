package com.foodapp.model;

public class PaymentRequest {
    private int orderId;
    private String name;
    private String address;
    private String phoneNumber;
    private String cardNumber;
    private String cvv;
    private String fromMM;
    private String fromYYYY;
    private String toMM;
    private String toYYYY;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private double amount;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getFromMM() {
        return fromMM;
    }

    public void setFromMM(String fromMM) {
        this.fromMM = fromMM;
    }

    public String getFromYYYY() {
        return fromYYYY;
    }

    public void setFromYYYY(String fromYYYY) {
        this.fromYYYY = fromYYYY;
    }

    public String getToMM() {
        return toMM;
    }

    public void setToMM(String toMM) {
        this.toMM = toMM;
    }

    public String getToYYYY() {
        return toYYYY;
    }

    public void setToYYYY(String toYYYY) {
        this.toYYYY = toYYYY;
    }
}
