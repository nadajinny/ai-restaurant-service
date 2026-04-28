package com.restaurant.backend.ai.dto;

import java.util.List;

public record AiRecommendResponse(
        List<AiRecommendationDto> recommendations
) {
}
