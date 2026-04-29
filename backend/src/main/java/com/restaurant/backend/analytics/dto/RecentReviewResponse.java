package com.restaurant.backend.analytics.dto;

import com.restaurant.backend.review.domain.ReviewStatus;
import java.time.LocalDateTime;

public record RecentReviewResponse(
        Long reviewId,
        Long menuId,
        String menuName,
        int rating,
        String content,
        ReviewStatus status,
        LocalDateTime createdAt
) {
}
