package com.restaurant.backend.ai.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AiReviewGenerateRequest(
        @NotNull(message = "menuId는 필수입니다.")
        Long menuId,
        @NotEmpty(message = "keywords는 최소 1개 이상이어야 합니다.")
        List<String> keywords
) {
}
