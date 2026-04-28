package com.restaurant.backend.analytics.dto;

public record MenuPerformanceResponse(
        Long menuId,
        String menuName,
        String category,
        int soldQuantity,
        int salesAmount
) {
}
