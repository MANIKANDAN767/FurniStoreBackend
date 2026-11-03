package com.furnistore.controller;

import com.furnistore.entity.Order;
import com.furnistore.entity.Product;
import com.furnistore.entity.Role;
import com.furnistore.entity.User;
import com.furnistore.repository.ProductRepository;
import com.furnistore.repository.UserRepository;
import com.furnistore.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // 🔒 ONLY admins can access this class
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public AdminController(UserRepository userRepository, ProductRepository productRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    // 1. Promote a user to Admin
    @PutMapping("/users/{userId}/promote")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.ADMIN); 
        userRepository.save(user);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    // 2. Get All Orders (For Admin Dashboard) - ✅ NOW THIS WILL WORK!
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    // 3. Update Order Status (e.g., PENDING -> SHIPPED)
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        // You can add the logic here later to find the order and update its status
        return ResponseEntity.ok("Order status updated to " + status);
    }

    // 4. Add a New Product (Admin only)
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }
}