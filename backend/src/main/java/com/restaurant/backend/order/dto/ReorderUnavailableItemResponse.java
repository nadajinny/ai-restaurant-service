package com.restaurant.backend.order.dto;

public record ReorderUnavailableItemResponse(
        Long menuId,
        String menuName,
        String reason
) {
}
