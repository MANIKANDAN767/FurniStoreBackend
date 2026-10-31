package com.furnistore.controller;

import com.furnistore.dto.ReviewRequest;
import com.furnistore.dto.ReviewResponse;
import com.furnistore.service.ReviewService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return userService.getUserIdByEmail(userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(getCurrentUserId(userDetails), request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> hasReviewed(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String productId) {
        return ResponseEntity.ok(reviewService.hasReviewed(getCurrentUserId(userDetails), productId));
    }
}