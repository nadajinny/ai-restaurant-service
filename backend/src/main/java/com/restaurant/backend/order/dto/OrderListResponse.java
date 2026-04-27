package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;
import java.time.LocalDateTime;

public record OrderListResponse(
        Long orderId,
        LocalDateTime orderedAt,
        Integer totalPrice,
        OrderStatus status,
        String representativeMenuName
) {
}
