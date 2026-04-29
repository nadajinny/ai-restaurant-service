package com.restaurant.backend.order.dto;

import jakarta.validation.constraints.Positive;
import java.util.List;

public record ReorderRequest(
        List<@Positive(message = "menuIds는 양수여야 합니다.") Long> menuIds
) {
}
