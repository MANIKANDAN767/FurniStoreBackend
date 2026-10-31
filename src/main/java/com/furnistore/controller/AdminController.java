package com.furnistore.controller;

import com.furnistore.dto.AddressRequest;
import com.furnistore.dto.OrderResponse;
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
@PreAuthorize("hasRole('ADMIN')") // 🔒 This ensures ONLY admins can access ANY endpoint in this class
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
     // ✅ CORRECT - Passing the Enum constant
        user.setRole(Role.ADMIN); // Ensure your User entity has a setRole method
        userRepository.save(user);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    // 2. Get All Orders (For Admin Dashboard)
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        // Note: We might need to update OrderService to have a 'findAll' method
        // For now, let's assume we want to see all orders from all users.
        // You can add a method in OrderRepository: List<Order> findAllByOrderByOrderDateDesc();
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    // 3. Update Order Status (e.g., PENDING -> SHIPPED)
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        // Logic to update order status would go here
        return ResponseEntity.ok("Order status updated to " + status);
    }

    // 4. Add a New Product (Admin only)
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }
}