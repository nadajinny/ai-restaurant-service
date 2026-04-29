package com.restaurant.backend.analytics.dto;

public record SoldOutMenuResponse(
        Long menuId,
        String menuName,
        String category
) {
}
