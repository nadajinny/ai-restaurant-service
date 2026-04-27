package com.restaurant.backend.coupon.dto;

public record CouponSummaryDto(
        Long id,
        String code,
        Integer discountAmount,
        String status
) {
}
