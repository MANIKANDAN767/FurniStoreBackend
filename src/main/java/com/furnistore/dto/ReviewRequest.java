package com.furnistore.dto;

public class ReviewRequest {
    private String productId;
    private Long orderId;
    private int rating; // 1-5
    private String comment;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}