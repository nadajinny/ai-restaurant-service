package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reviews")
@Tag(name = "관리자 리뷰", description = "관리자 리뷰 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "전체 리뷰 조회", description = "관리자 권한으로 리뷰 목록을 조회합니다.")
    public ApiResponse<List<ReviewResponse>> getReviews() {
        return ApiResponse.success(reviewService.getAdminReviews());
    }

    @PatchMapping("/{reviewId}/hide")
    @Operation(summary = "리뷰 숨김 처리", description = "관리자 권한으로 부적절한 리뷰를 숨김 처리합니다.")
    public ApiResponse<ReviewResponse> hideReview(@PathVariable Long reviewId) {
        return ApiResponse.success("리뷰가 숨김 처리되었습니다.", reviewService.hideReview(reviewId));
    }
}
