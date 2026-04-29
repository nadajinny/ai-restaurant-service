package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        LocalDateTime orderedAt,
        Integer totalPrice,
        OrderStatus status,
        List<OrderDetailItemResponse> items
) {
}
