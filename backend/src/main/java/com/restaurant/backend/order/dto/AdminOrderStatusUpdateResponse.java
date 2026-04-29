package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;

public record AdminOrderStatusUpdateResponse(
        Long orderId,
        OrderStatus status
) {
}
