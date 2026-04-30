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
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI", description = "AI 추천 및 리뷰 보조 API")
public class AiController {

    private final AiService aiService;
    private final CurrentUserService currentUserService;

    public AiController(AiService aiService, CurrentUserService currentUserService) {
        this.aiService = aiService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/ai/recommend")
    @Operation(summary = "AI 메뉴 추천", description = "입력한 취향 또는 요청 문장을 기반으로 메뉴를 추천합니다.")
    public ApiResponse<AiRecommendResponse> recommend(@Valid @RequestBody AiRecommendRequest request) {
        return ApiResponse.success(aiService.recommend(request));
    }

    @GetMapping("/ai/personalized-recommendations")
    @Operation(summary = "개인화 추천 조회", description = "로그인한 사용자의 주문/선호 이력을 기반으로 맞춤 추천을 제공합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AiPersonalizedRecommendationResponse> getPersonalizedRecommendations(Authentication authentication) {
        return ApiResponse.success(
                aiService.getPersonalizedRecommendations(currentUserService.getCurrentUserId(authentication))
        );
    }

    @PostMapping("/ai/emotion-recommend")
    @Operation(summary = "감정 기반 추천", description = "현재 감정 상태를 바탕으로 어울리는 메뉴를 추천합니다.")
    public ApiResponse<AiEmotionRecommendResponse> emotionRecommend(
            @Valid @RequestBody AiEmotionRecommendRequest request
    ) {
        return ApiResponse.success(aiService.emotionRecommend(request));
    }

    @PostMapping("/ai/review-generate")
    @Operation(summary = "AI 리뷰 초안 생성", description = "주문 및 메뉴 정보를 바탕으로 리뷰 초안을 생성합니다.")
    public ApiResponse<AiReviewGenerateResponse> generateReview(
            @Valid @RequestBody AiReviewGenerateRequest request
    ) {
        return ApiResponse.success(aiService.generateReview(request));
    }

    @GetMapping("/ai/menus/{menuId}/review-summary")
    @Operation(summary = "메뉴 리뷰 요약", description = "특정 메뉴에 대한 리뷰를 AI가 요약합니다.")
    public ApiResponse<AiReviewSummaryResponse> getReviewSummary(@PathVariable Long menuId) {
        return ApiResponse.success(aiService.getReviewSummary(menuId));
    }

    @GetMapping("/admin/ai/new-menu-recommendations")
    @Operation(summary = "신메뉴 추천 조회", description = "관리자 권한으로 AI 기반 신메뉴 추천 결과를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AiNewMenuRecommendationsResponse> getNewMenuRecommendations() {
        return ApiResponse.success(aiService.getNewMenuRecommendations());
    }

    @PostMapping("/api/v1/ai/recommendations/mock")
    @Operation(summary = "레거시 AI 추천(Mock)", description = "기존 호환 경로에서 동작하는 Mock 추천 API입니다.")
    public ApiResponse<AiRecommendationDto> recommendLegacy(@Valid @RequestBody AiRecommendRequest request) {
        return ApiResponse.success(aiService.recommendLegacy(request));
    }
}
