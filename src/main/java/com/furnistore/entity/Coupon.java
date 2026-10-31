package com.furnistore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g., SUMMER20

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // PERCENTAGE or FIXED

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue; // e.g., 20.00 for 20% or $20 off

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private int maxUsage; // Max times this coupon can be used total

    @Column(nullable = false)
    private int currentUsage = 0; // How many times it has been used

    @Column(nullable = false)
    private boolean active = true;

    public enum DiscountType {
        PERCENTAGE, FIXED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }
    
    public int getCurrentUsage() { return currentUsage; }
    public void setCurrentUsage(int currentUsage) { this.currentUsage = currentUsage; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}