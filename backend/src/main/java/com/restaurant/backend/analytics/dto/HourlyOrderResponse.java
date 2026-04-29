package com.restaurant.backend.analytics.dto;

public record HourlyOrderResponse(
        int hour,
        long orderCount
) {
}
