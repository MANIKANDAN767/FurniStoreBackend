package com.furnistore.controller;
import com.furnistore.dto.OrderResponse; // ✅ ADD THIS LINE
import com.furnistore.dto.CheckoutRequest; // ✅ CORRECT
import com.furnistore.entity.Order;
import com.furnistore.entity.User;
import com.furnistore.service.OrderService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody CheckoutRequest request, Authentication authentication) {
        // Get the logged-in user's email from the JWT token
        String email = authentication.getName();
        
        // Fetch the user from the database
        User user = userService.getUserByEmail(email);
        
        // Pass the CheckoutRequest to the service
        Order order = orderService.checkout(user.getId(), request);
        
        // Return the response (adjust this if your OrderResponse constructor takes different arguments)
        return ResponseEntity.ok(new OrderResponse(order)); 
    }
}