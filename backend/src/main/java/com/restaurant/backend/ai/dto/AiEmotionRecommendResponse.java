package com.restaurant.backend.ai.dto;

import java.util.List;

public record AiEmotionRecommendResponse(
        List<AiRecommendationDto> recommendations
) {
}
