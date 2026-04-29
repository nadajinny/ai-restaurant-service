package com.restaurant.backend.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
        @NotNull(message = "orderId는 필수입니다.")
        Long orderId,
        MockPaymentResult mockResult
) {
}
