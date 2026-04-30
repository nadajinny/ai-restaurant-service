package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.MenuReviewResponse;
import com.restaurant.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menus")
@Tag(name = "리뷰", description = "리뷰 조회 및 관리 API")
public class MenuReviewController {

    private final ReviewService reviewService;

    public MenuReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{menuId}/reviews")
    @Operation(summary = "메뉴 리뷰 목록 조회", description = "특정 메뉴에 작성된 리뷰 목록을 조회합니다.")
    public ApiResponse<List<MenuReviewResponse>> getMenuReviews(@PathVariable Long menuId) {
        return ApiResponse.success(reviewService.getMenuReviews(menuId));
    }
}
