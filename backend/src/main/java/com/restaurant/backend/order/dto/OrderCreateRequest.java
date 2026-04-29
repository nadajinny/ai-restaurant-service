package com.restaurant.backend.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateRequest(
        @NotEmpty(message = "items는 최소 1개 이상이어야 합니다.")
        List<@Valid OrderCreateItemRequest> items,
        String couponCode
) {
}
