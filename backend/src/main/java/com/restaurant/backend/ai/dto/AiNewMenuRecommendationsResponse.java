package com.restaurant.backend.ai.dto;

import java.util.List;

public record AiNewMenuRecommendationsResponse(
        List<AiNewMenuRecommendationDto> recommendations
) {
}
