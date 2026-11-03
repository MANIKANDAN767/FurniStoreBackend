package com.furnistore.controller;

import com.furnistore.dto.AuthResponse;
import com.furnistore.dto.LoginRequest;
import com.furnistore.dto.RegisterRequest;
import com.furnistore.dto.UserResponse;
import com.furnistore.entity.User;
import com.furnistore.security.JwtService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                          JwtService jwtService, 
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // ✅ 1. REGISTER ENDPOINT (Fixed Token Generation)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // 1. Save the user to the database
        User savedUser = userService.register(request);
        
        // 2. ✅ FIX: Build a UserDetails object because generateToken() expects it
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name())
                .build();

        // 3. Generate JWT Token using the UserDetails object
        String token = jwtService.generateToken(userDetails);

        // 4. Build the response for the frontend
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, response));
    }

    // ✅ 2. LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        
        // 1. Authenticate the user with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        // 3. Fetch full user details from database (Returns a 'User' entity)
        User user = userService.getUserByEmail(request.getEmail());

        // 4. Convert the 'User' entity into a 'UserResponse' DTO for the frontend
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());

        // 5. Return the token and user data
        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }
}