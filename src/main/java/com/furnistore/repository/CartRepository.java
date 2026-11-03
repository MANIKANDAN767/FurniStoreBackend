package com.furnistore.repository;

import com.furnistore.entity.Cart;
import com.furnistore.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // ✅ This method is required to find a cart by the user's ID
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUser(User user);
}