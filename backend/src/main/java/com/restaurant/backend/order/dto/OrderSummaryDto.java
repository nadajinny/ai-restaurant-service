package com.restaurant.backend.order.dto;

public record OrderSummaryDto(
        Long id,
        String status,
        Integer totalPrice
) {
}
