package com.furnistore.repository;

import com.furnistore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // ✅ EXACT casing is required! 'findBy' + 'UserId'
    List<Order> findByUserId(Long userId);

    // ✅ Fetch all orders sorted by newest first (for Admin Dashboard)
    List<Order> findAllByOrderByOrderDateDesc();
    
}