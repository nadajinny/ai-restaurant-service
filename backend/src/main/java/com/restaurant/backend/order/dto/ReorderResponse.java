package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;
import java.util.List;

public record ReorderResponse(
        Long orderId,
        OrderStatus status,
        Integer totalPrice,
        List<ReorderUnavailableItemResponse> unavailableItems
) {
}
