package com.restaurant.backend.review.dto;

import com.restaurant.backend.review.domain.ReviewStatus;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Long userId,
        Long menuId,
        Long orderId,
        String content,
        Integer rating,
        boolean aiGenerated,
        ReviewStatus status,
        LocalDateTime createdAt
) {
}
