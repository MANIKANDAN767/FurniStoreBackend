package com.furnistore.controller;

import com.furnistore.dto.AddToWishlistRequest;
import com.furnistore.dto.WishlistResponse;
import com.furnistore.service.UserService;
import com.furnistore.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return userService.getUserIdByEmail(userDetails.getUsername());
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddToWishlistRequest request) {
        return ResponseEntity.ok(wishlistService.addToWishlist(getCurrentUserId(userDetails), request));
    }

    @GetMapping
    public ResponseEntity<WishlistResponse> viewWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.viewWishlist(getCurrentUserId(userDetails)));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId) {
        return ResponseEntity.ok(wishlistService.removeFromWishlist(getCurrentUserId(userDetails), productId));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isInWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId) {
        return ResponseEntity.ok(wishlistService.isInWishlist(getCurrentUserId(userDetails), productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.clearWishlist(getCurrentUserId(userDetails));
        return ResponseEntity.noContent().build();
    }
}