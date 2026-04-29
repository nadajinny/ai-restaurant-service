package com.restaurant.backend.analytics.dto;

public record PopularMenuResponse(
        Long menuId,
        String menuName,
        String category,
        int soldQuantity,
        int salesAmount
) {
}
