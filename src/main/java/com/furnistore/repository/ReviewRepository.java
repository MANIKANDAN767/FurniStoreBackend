package com.furnistore.repository;

import com.furnistore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProductIdOrderByCreatedAtDesc(String productId);
    
    Optional<Review> findByUserIdAndProductId(Long userId, String productId);
    
    boolean existsByUserIdAndProductId(Long userId, String productId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingForProduct(String productId);
    
    long countByProductId(String productId);
}