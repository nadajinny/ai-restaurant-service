package com.restaurant.backend.review.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.review.dto.MenuReviewResponse;
import com.restaurant.backend.review.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menus")
public class MenuReviewController {

    private final ReviewService reviewService;

    public MenuReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{menuId}/reviews")
    public ApiResponse<List<MenuReviewResponse>> getMenuReviews(@PathVariable Long menuId) {
        return ApiResponse.success(reviewService.getMenuReviews(menuId));
    }
}
