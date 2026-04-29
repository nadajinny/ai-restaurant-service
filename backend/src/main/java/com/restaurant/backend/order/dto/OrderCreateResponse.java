package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;

public record OrderCreateResponse(
        Long orderId,
        OrderStatus status,
        Integer totalPrice
) {
}
