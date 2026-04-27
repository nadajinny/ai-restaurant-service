package com.restaurant.backend.coupon.dto;

public record CouponApplyResponse(
        Long orderId,
        Long couponId,
        String couponCode,
        Integer originalTotalPrice,
        Integer discountAmount,
        Integer finalTotalPrice
) {
}
