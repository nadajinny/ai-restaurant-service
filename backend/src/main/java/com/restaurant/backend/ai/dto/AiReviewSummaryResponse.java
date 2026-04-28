package com.restaurant.backend.ai.dto;

public record AiReviewSummaryResponse(
        Long menuId,
        String summary
) {
}
