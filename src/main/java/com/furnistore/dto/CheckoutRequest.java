package com.furnistore.dto;

public class CheckoutRequest {

    // ✅ This matches the 'addressId' sent from your React frontend
    private Long addressId; 
    
    // ✅ This matches the 'couponCode' (can be null)
    private String couponCode; 

    // Default constructor (Required for Spring to read the JSON)
    public CheckoutRequest() {
    }

    // Getters and Setters
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}