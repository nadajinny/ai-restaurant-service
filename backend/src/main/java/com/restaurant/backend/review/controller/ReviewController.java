package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewSummaryDto;
import com.restaurant.backend.review.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/sample")
    public ApiResponse<ReviewSummaryDto> getSampleReview() {
        return ApiResponse.success(reviewService.getSampleReview());
    }
}
