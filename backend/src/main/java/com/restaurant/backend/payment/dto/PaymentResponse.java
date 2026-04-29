package com.restaurant.backend.payment.dto;

import com.restaurant.backend.payment.domain.PaymentStatus;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        Integer amount,
        PaymentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
