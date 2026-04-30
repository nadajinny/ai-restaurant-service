package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.ReviewCreateRequest;
import com.restaurant.backend.review.dto.ReviewResponse;
import com.restaurant.backend.review.dto.ReviewUpdateRequest;
import com.restaurant.backend.review.service.ReviewService;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@Tag(name = "리뷰", description = "리뷰 조회 및 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    public ReviewController(ReviewService reviewService, CurrentUserService currentUserService) {
        this.reviewService = reviewService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "리뷰 작성", description = "로그인한 사용자가 리뷰를 작성합니다.")
    public ApiResponse<ReviewResponse> createReview(
            Authentication authentication,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ApiResponse.success(
                "리뷰가 작성되었습니다.",
                reviewService.createReview(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "로그인한 사용자가 본인 리뷰를 수정합니다.")
    public ApiResponse<ReviewResponse> updateReview(
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
    @Operation(summary = "리뷰 삭제", description = "로그인한 사용자가 본인 리뷰를 삭제합니다.")
    public ApiResponse<Void> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(currentUserService.getCurrentUserId(authentication), reviewId);
        return ApiResponse.success("리뷰가 삭제되었습니다.", null);
    }
}
