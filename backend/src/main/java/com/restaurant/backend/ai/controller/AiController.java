package com.restaurant.backend.ai.controller;

import com.restaurant.backend.ai.dto.AiEmotionRecommendRequest;
import com.restaurant.backend.ai.dto.AiEmotionRecommendResponse;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationsResponse;
import com.restaurant.backend.ai.dto.AiPersonalizedRecommendationResponse;
import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendRequest;
import com.restaurant.backend.ai.dto.AiRecommendResponse;
import com.restaurant.backend.ai.dto.AiReviewGenerateRequest;
import com.restaurant.backend.ai.dto.AiReviewGenerateResponse;
import com.restaurant.backend.ai.dto.AiReviewSummaryResponse;
import com.restaurant.backend.ai.service.AiService;
import com.restaurant.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import com.restaurant.backend.user.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    private final AiService aiService;
    private final CurrentUserService currentUserService;

    public AiController(AiService aiService, CurrentUserService currentUserService) {
        this.aiService = aiService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/ai/recommend")
    public ApiResponse<AiRecommendResponse> recommend(@Valid @RequestBody AiRecommendRequest request) {
        return ApiResponse.success(aiService.recommend(request));
    }

    @GetMapping("/ai/personalized-recommendations")
    public ApiResponse<AiPersonalizedRecommendationResponse> getPersonalizedRecommendations(
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        return ApiResponse.success(
                aiService.getPersonalizedRecommendations(currentUserService.getCurrentUserId(authentication))
        );
    }

    @PostMapping("/ai/emotion-recommend")
    public ApiResponse<AiEmotionRecommendResponse> emotionRecommend(
            @Valid @RequestBody AiEmotionRecommendRequest request
    ) {
        return ApiResponse.success(aiService.emotionRecommend(request));
    }

    @PostMapping("/ai/review-generate")
    public ApiResponse<AiReviewGenerateResponse> generateReview(
            @Valid @RequestBody AiReviewGenerateRequest request
    ) {
        return ApiResponse.success(aiService.generateReview(request));
    }

    @GetMapping("/ai/menus/{menuId}/review-summary")
    public ApiResponse<AiReviewSummaryResponse> getReviewSummary(@PathVariable Long menuId) {
        return ApiResponse.success(aiService.getReviewSummary(menuId));
    }

    @GetMapping("/admin/ai/new-menu-recommendations")
    public ApiResponse<AiNewMenuRecommendationsResponse> getNewMenuRecommendations() {
        return ApiResponse.success(aiService.getNewMenuRecommendations());
    }

    @PostMapping("/api/v1/ai/recommendations/mock")
    public ApiResponse<AiRecommendationDto> recommendLegacy(@Valid @RequestBody AiRecommendRequest request) {
        return ApiResponse.success(aiService.recommendLegacy(request));
    }
}
