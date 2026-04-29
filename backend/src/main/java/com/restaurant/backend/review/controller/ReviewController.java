package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewCreateRequest;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.dto.ReviewUpdateRequest;
import com.restaurant.backend.review.service.ReviewService;
import com.restaurant.backend.user.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    public ReviewController(ReviewService reviewService, CurrentUserService currentUserService) {
        this.reviewService = reviewService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ApiResponse.success(
                "리뷰가 작성되었습니다.",
                reviewService.createReview(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
            @RequestParam(required = false) Long userId,
            @PathVariable Long reviewId,
            Authentication authentication,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        return ApiResponse.success(
                "리뷰가 수정되었습니다.",
                reviewService.updateReview(currentUserService.getCurrentUserId(authentication), reviewId, request)
        );
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(currentUserService.getCurrentUserId(authentication), reviewId);
        return ApiResponse.success("리뷰가 삭제되었습니다.", null);
    }
}
