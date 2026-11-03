package com.furnistore.dto;

public class LoginRequest {
    private String email;
    private String password;

    // ✅ ADD THIS EXPLICIT NO-ARG CONSTRUCTOR FOR JACKSON
    public LoginRequest() {}

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}