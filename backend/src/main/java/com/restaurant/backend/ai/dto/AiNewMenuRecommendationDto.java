package com.restaurant.backend.ai.dto;

public record AiNewMenuRecommendationDto(
        String name,
        String category,
        String reason
) {
}
