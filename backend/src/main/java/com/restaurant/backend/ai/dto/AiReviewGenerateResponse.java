package com.restaurant.backend.ai.dto;

public record AiReviewGenerateResponse(
        Long menuId,
        String reviewDraft,
        boolean aiGenerated
) {
}
