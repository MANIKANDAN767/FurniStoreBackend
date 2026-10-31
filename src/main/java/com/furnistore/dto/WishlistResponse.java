package com.furnistore.dto;

import java.util.List;

public class WishlistResponse {
    private Long wishlistId;
    private List<WishlistItemResponse> items;
    private Integer totalItems;

    public Long getWishlistId() { return wishlistId; }
    public void setWishlistId(Long wishlistId) { this.wishlistId = wishlistId; }
    public List<WishlistItemResponse> getItems() { return items; }
    public void setItems(List<WishlistItemResponse> items) { this.items = items; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}