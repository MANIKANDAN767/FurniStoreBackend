package com.furnistore.controller;

import com.furnistore.dto.CheckoutRequest;
import com.furnistore.dto.OrderResponse;
import com.furnistore.service.OrderService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return userService.getUserIdByEmail(userDetails.getUsername());
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(getCurrentUserId(userDetails), request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrderHistory(getCurrentUserId(userDetails)));
    }
}