package com.furnistore.dto;

public class CategoryRequest {
    private String name;
    private String image; // Optional

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}