package com.furnistore.controller;

import com.furnistore.dto.AddToCartRequest;
import com.furnistore.dto.UpdateQuantityRequest;
import com.furnistore.entity.Cart;
import com.furnistore.entity.User;
import com.furnistore.service.CartService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.viewCart(user.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.addToCart(user.getId(), request));
    }

    // ✅ FIX: This now matches the frontend's PUT /api/cart/items/{id}
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> updateQuantity(@PathVariable Long cartItemId, @RequestBody UpdateQuantityRequest request, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.updateQuantity(user.getId(), cartItemId, request));
    }

    // ✅ FIX: This now matches the frontend's DELETE /api/cart/items/{id}
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(cartService.removeFromCart(user.getId(), cartItemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        cartService.clearCart(user.getId());
        return ResponseEntity.ok("Cart cleared successfully");
    }
}