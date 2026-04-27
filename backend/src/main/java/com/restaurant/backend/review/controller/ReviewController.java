package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewCreateRequest;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.dto.ReviewUpdateRequest;
import com.restaurant.backend.review.service.ReviewService;
import jakarta.validation.Valid;
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

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(
            @RequestParam Long userId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success("리뷰가 작성되었습니다.", reviewService.createReview(userId, request));
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
            @RequestParam Long userId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success("리뷰가 수정되었습니다.", reviewService.updateReview(userId, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@RequestParam Long userId, @PathVariable Long reviewId) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        reviewService.deleteReview(userId, reviewId);
        return ApiResponse.success("리뷰가 삭제되었습니다.", null);
    }
}
