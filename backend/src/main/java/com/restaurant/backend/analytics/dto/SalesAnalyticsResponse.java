package com.restaurant.backend.analytics.dto;

public record SalesAnalyticsResponse(
        long completedOrderCount,
        int totalSales,
        int averageOrderAmount
) {
}
