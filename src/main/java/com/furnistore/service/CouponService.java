package com.furnistore.service;

import com.furnistore.entity.Coupon;
import com.furnistore.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * Validates a coupon and returns the discount amount.
     * Throws RuntimeException if invalid.
     */
    @Transactional
    public BigDecimal validateAndApplyCoupon(String code, BigDecimal cartTotal) {
    	if (code == null || code.isBlank()) {
    	    return BigDecimal.ZERO;
    	}

        Coupon coupon = couponRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        // Check if active
        if (!coupon.isActive()) {
            throw new RuntimeException("This coupon is no longer active");
        }

        // Check expiry
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This coupon has expired");
        }

        // Check usage limit
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new RuntimeException("This coupon has reached its maximum usage limit");
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = cartTotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            // Cap discount at cart total
            if (discount.compareTo(cartTotal) > 0) {
                discount = cartTotal;
            }
        } else {
            // FIXED amount
            discount = coupon.getDiscountValue();
            if (discount.compareTo(cartTotal) > 0) {
                discount = cartTotal; // Don't give negative totals
            }
        }

        // Increment usage count
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        couponRepository.save(coupon);

        return discount;
    }
}