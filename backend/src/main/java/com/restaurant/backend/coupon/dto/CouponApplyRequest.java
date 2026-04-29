package com.restaurant.backend.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CouponApplyRequest(
        @NotNull(message = "orderId는 필수입니다.")
        Long orderId,
        @NotBlank(message = "couponCode는 필수입니다.")
        String couponCode
) {
}
