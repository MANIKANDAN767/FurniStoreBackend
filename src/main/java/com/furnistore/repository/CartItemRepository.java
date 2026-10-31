package com.furnistore.repository;

import com.furnistore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Note: productId is String because Product.id is String
    Optional<CartItem> findByCartIdAndProductId(Long cartId, String productId);
}