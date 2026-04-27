package com.restaurant.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AiRecommendationRequest(
        @NotBlank(message = "message is required")
        String message
) {
}
