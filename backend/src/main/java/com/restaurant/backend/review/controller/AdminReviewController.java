package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ApiResponse<List<ReviewResponse>> getReviews() {
        return ApiResponse.success(reviewService.getAdminReviews());
    }

    @PatchMapping("/{reviewId}/hide")
    public ApiResponse<ReviewResponse> hideReview(@PathVariable Long reviewId) {
        return ApiResponse.success("리뷰가 숨김 처리되었습니다.", reviewService.hideReview(reviewId));
    }
}
