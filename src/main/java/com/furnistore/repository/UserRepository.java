package com.furnistore.repository;

import com.furnistore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ✅ This method is required to find users by email
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}