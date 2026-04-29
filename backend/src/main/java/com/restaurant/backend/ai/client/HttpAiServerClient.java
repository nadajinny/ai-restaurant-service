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
import com.restaurant.backend.config.AiServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class HttpAiServerClient implements AiServerClient {

    private final RestTemplate aiRestTemplate;
    private final AiServerProperties aiServerProperties;

    public HttpAiServerClient(RestTemplate aiRestTemplate, AiServerProperties aiServerProperties) {
        this.aiRestTemplate = aiRestTemplate;
        this.aiServerProperties = aiServerProperties;
    }

    @Override
    public AiRecommendResponse recommend(AiRecommendRequest request) {
        return aiRestTemplate.postForObject(
                buildUrl("/ai/recommend"),
                request,
                AiRecommendResponse.class
        );
    }

    @Override
    public AiPersonalizedRecommendationResponse getPersonalizedRecommendations(Long userId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildUrl("/ai/personalized-recommendations"))
                .queryParamIfPresent("userId", java.util.Optional.ofNullable(userId))
                .toUriString();
        return aiRestTemplate.getForObject(url, AiPersonalizedRecommendationResponse.class);
    }

    @Override
    public AiEmotionRecommendResponse emotionRecommend(AiEmotionRecommendRequest request) {
        return aiRestTemplate.postForObject(
                buildUrl("/ai/emotion-recommend"),
                request,
                AiEmotionRecommendResponse.class
        );
    }

    @Override
    public AiReviewGenerateResponse generateReview(AiReviewGenerateRequest request) {
        return aiRestTemplate.postForObject(
                buildUrl("/ai/review-generate"),
                request,
                AiReviewGenerateResponse.class
        );
    }

    @Override
    public AiReviewSummaryResponse getReviewSummary(Long menuId) {
        return aiRestTemplate.getForObject(
                buildUrl("/ai/menus/" + menuId + "/review-summary"),
                AiReviewSummaryResponse.class
        );
    }

    @Override
    public AiNewMenuRecommendationsResponse getNewMenuRecommendations() {
        return aiRestTemplate.getForObject(
                buildUrl("/admin/ai/new-menu-recommendations"),
                AiNewMenuRecommendationsResponse.class
        );
    }

    private String buildUrl(String path) {
        return aiServerProperties.getBaseUrl() + path;
    }
}
