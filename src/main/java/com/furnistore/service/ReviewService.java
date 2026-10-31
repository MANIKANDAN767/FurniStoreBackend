package com.furnistore.service;

import com.furnistore.dto.ReviewRequest;
import com.furnistore.dto.ReviewResponse;
import com.furnistore.entity.Order;
import com.furnistore.entity.Product;
import com.furnistore.entity.Review;
import com.furnistore.entity.User;
import com.furnistore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
                         ProductRepository productRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponse addReview(Long userId, ReviewRequest request) {
        // 1. Validate user, product, and order exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. Verify the order belongs to this user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only review orders you placed");
        }

        // 3. Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new RuntimeException("You have already reviewed this product");
        }

        // 4. Validate rating range
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // 5. Create and save review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        // 6. Update product's average rating and count
        updateProductRating(request.getProductId());

        return mapToResponse(savedReview);
    }

    public List<ReviewResponse> getProductReviews(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean hasReviewed(Long userId, String productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    private void updateProductRating(String productId) {
        Double avgRating = reviewRepository.getAverageRatingForProduct(productId);
        long count = reviewRepository.countByProductId(productId);

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setRating(avgRating != null ? 
                BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO);
            product.setReviewCount((int) count);
            productRepository.save(product);
        }
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserName(review.getUser().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}