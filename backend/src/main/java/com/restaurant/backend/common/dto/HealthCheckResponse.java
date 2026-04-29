package com.restaurant.backend.common.dto;

public record HealthCheckResponse(
        String status,
        String service
) {
}
