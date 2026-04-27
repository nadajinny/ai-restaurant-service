package com.restaurant.backend.coupon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AdminCouponRequest(
        @NotBlank(message = "code는 필수입니다.")
        String code,
        @NotBlank(message = "name은 필수입니다.")
        String name,
        @Min(value = 0, message = "discountAmount는 0 이상이어야 합니다.")
        Integer discountAmount,
        @Min(value = 0, message = "discountRate는 0 이상이어야 합니다.")
        Integer discountRate,
        @Min(value = 0, message = "maxDiscountAmount는 0 이상이어야 합니다.")
        Integer maxDiscountAmount,
        @NotNull(message = "minOrderAmount는 필수입니다.")
        @Min(value = 0, message = "minOrderAmount는 0 이상이어야 합니다.")
        Integer minOrderAmount,
        @NotNull(message = "availableFrom은 필수입니다.")
        LocalDateTime availableFrom,
        @NotNull(message = "availableTo는 필수입니다.")
        LocalDateTime availableTo,
        @NotNull(message = "availableCount는 필수입니다.")
        @Min(value = 0, message = "availableCount는 0 이상이어야 합니다.")
        Integer availableCount,
        boolean active
) {
}
