package com.restaurant.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiEmotionRecommendRequest(
        @NotBlank(message = "emotion은 필수입니다.")
        String emotion,
        @Size(max = 500, message = "context는 500자를 초과할 수 없습니다.")
        String context
) {
}
