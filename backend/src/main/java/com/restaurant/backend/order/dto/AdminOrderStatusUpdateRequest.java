package com.restaurant.backend.order.dto;

import com.restaurant.backend.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record AdminOrderStatusUpdateRequest(
        @NotNull(message = "status는 필수입니다.")
        OrderStatus status
) {
}
