package com.restaurant.backend.ai.dto;

public record AiRecommendationDto(
        Long menuId,
        String name,
        String reason
) {
}
