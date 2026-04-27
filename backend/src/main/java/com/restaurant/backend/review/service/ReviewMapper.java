package com.restaurant.backend.review.service;

import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.dto.MenuReviewResponse;
import com.restaurant.backend.review.dto.ReviewResponse;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getMenu().getId(),
                review.getOrder().getId(),
                review.getContent(),
                review.getRating(),
                review.isAiGenerated(),
                review.getStatus(),
                review.getCreatedAt()
        );
    }

    public MenuReviewResponse toMenuReviewResponse(Review review) {
        return new MenuReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getContent(),
                review.getRating(),
                review.isAiGenerated(),
                review.getCreatedAt()
        );
    }
}
