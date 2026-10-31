package com.furnistore.controller;

import com.furnistore.dto.AuthResponse;
import com.furnistore.dto.LoginRequest;
import com.furnistore.dto.RegisterRequest;
import com.furnistore.dto.UserResponse;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    // Add this endpoint to AuthController
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @org.springframework.security.core.annotation.AuthenticationPrincipal 
            org.springframework.security.core.userdetails.User userDetails) {
        
        // userDetails.getUsername() contains the email from the validated JWT
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
    }
}