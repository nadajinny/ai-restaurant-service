package com.restaurant.backend.review.dto;

import java.time.LocalDateTime;

public record MenuReviewResponse(
        Long reviewId,
        Long userId,
        String content,
        Integer rating,
        boolean aiGenerated,
        LocalDateTime createdAt
) {
}
