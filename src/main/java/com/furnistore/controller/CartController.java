package com.furnistore.controller;

import com.furnistore.dto.AddToCartRequest;
import com.furnistore.dto.CartResponse;
import com.furnistore.dto.UpdateQuantityRequest;
import com.furnistore.service.CartService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    private Long getCurrentUserId(UserDetails userDetails) {
        return userService.getUserIdByEmail(userDetails.getUsername());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(getCurrentUserId(userDetails), request));
    }

    @GetMapping
    public ResponseEntity<CartResponse> viewCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.viewCart(getCurrentUserId(userDetails)));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(getCurrentUserId(userDetails), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeFromCart(getCurrentUserId(userDetails), cartItemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(getCurrentUserId(userDetails));
        return ResponseEntity.noContent().build();
    }
}