package com.restaurant.backend.ai.client;

import com.restaurant.backend.ai.dto.AiEmotionRecommendRequest;
import com.restaurant.backend.ai.dto.AiEmotionRecommendResponse;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationsResponse;
import com.restaurant.backend.ai.dto.AiPersonalizedRecommendationResponse;
import com.restaurant.backend.ai.dto.AiRecommendRequest;
import com.restaurant.backend.ai.dto.AiRecommendResponse;
import com.restaurant.backend.ai.dto.AiReviewGenerateRequest;
import com.restaurant.backend.ai.dto.AiReviewGenerateResponse;
import com.restaurant.backend.ai.dto.AiReviewSummaryResponse;

public interface AiServerClient {

    AiRecommendResponse recommend(AiRecommendRequest request);

    AiPersonalizedRecommendationResponse getPersonalizedRecommendations(Long userId);

    AiEmotionRecommendResponse emotionRecommend(AiEmotionRecommendRequest request);

    AiReviewGenerateResponse generateReview(AiReviewGenerateRequest request);

    AiReviewSummaryResponse getReviewSummary(Long menuId);

    AiNewMenuRecommendationsResponse getNewMenuRecommendations();
}
