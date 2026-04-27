package com.restaurant.backend.review.service;

import com.restaurant.backend.review.dto.ReviewSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    public ReviewSummaryDto getSampleReview() {
        return new ReviewSummaryDto(1L, 1L, 5);
    }
}
