package com.furnistore.dto;

public class CheckoutRequest {
    private Long addressId;
    private String couponCode; // ✅ ADD THIS FIELD

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    
 // In CheckoutRequest.java
    public String getCouponCode() { 
        return couponCode != null ? couponCode.trim() : null; 
    }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}