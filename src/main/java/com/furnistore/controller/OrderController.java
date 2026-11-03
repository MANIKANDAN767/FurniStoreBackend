
package com.furnistore.controller;

import com.furnistore.dto.OrderResponse;
import com.furnistore.dto.CheckoutRequest;
import com.furnistore.entity.Order;
import com.furnistore.entity.User;
import com.furnistore.service.OrderService;
import com.furnistore.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(
            OrderService orderService,
            UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Existing checkout endpoint
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestBody CheckoutRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        Order order = orderService.checkout(user.getId(), request);

        return ResponseEntity.ok(new OrderResponse(order));
    }

    // NEW: Get orders belonging to logged-in customer
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        List<Order> orders =
                orderService.getUserOrders(user.getId());

        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
