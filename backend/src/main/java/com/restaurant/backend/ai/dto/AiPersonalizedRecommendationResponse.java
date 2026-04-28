package com.restaurant.backend.ai.dto;

import java.util.List;

public record AiPersonalizedRecommendationResponse(
        Long userId,
        List<AiRecommendationDto> recommendations
) {
}
