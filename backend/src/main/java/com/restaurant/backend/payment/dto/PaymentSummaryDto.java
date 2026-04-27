package com.restaurant.backend.payment.dto;

public record PaymentSummaryDto(
        Long id,
        Long orderId,
        Integer amount,
        String status
) {
}
