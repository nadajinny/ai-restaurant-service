package com.restaurant.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AiRecommendRequest(
        @NotBlank(message = "message는 필수입니다.")
        String message
) {
}
