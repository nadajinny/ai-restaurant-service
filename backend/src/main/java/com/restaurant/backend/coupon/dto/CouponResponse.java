package com.restaurant.backend.coupon.dto;

import java.time.LocalDateTime;

public record CouponResponse(
        Long couponId,
        String code,
        String name,
        Integer discountAmount,
        Integer discountRate,
        Integer maxDiscountAmount,
        Integer minOrderAmount,
        LocalDateTime availableFrom,
        LocalDateTime availableTo,
        Integer availableCount,
        boolean active
) {
}
