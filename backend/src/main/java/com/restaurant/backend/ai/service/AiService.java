package com.restaurant.backend.ai.service;

import com.restaurant.backend.ai.client.AiServerClient;
import com.restaurant.backend.ai.dto.AiEmotionRecommendRequest;
import com.restaurant.backend.ai.dto.AiEmotionRecommendResponse;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationDto;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationsResponse;
import com.restaurant.backend.ai.dto.AiPersonalizedRecommendationResponse;
import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendRequest;
import com.restaurant.backend.ai.dto.AiRecommendResponse;
import com.restaurant.backend.ai.dto.AiReviewGenerateRequest;
import com.restaurant.backend.ai.dto.AiReviewGenerateResponse;
import com.restaurant.backend.ai.dto.AiReviewSummaryResponse;
import com.restaurant.backend.common.cache.CacheNames;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiService {

    private final AiServerClient aiServerClient;
    private final MenuRepository menuRepository;

    public AiService(AiServerClient aiServerClient, MenuRepository menuRepository) {
        this.aiServerClient = aiServerClient;
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public AiRecommendResponse recommend(AiRecommendRequest request) {
        validateTextInput(request.message(), "message");

        try {
            return new AiRecommendResponse(filterRecommendableMenus(aiServerClient.recommend(request).recommendations()));
        } catch (Exception exception) {
            return new AiRecommendResponse(buildFallbackRecommendations("기본 추천 메뉴입니다."));
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PERSONALIZED_RECOMMENDATIONS, key = "#userId")
    public AiPersonalizedRecommendationResponse getPersonalizedRecommendations(Long userId) {
        try {
            AiPersonalizedRecommendationResponse response = aiServerClient.getPersonalizedRecommendations(userId);
            return new AiPersonalizedRecommendationResponse(
                    userId,
                    filterRecommendableMenus(response.recommendations())
            );
        } catch (Exception exception) {
            return new AiPersonalizedRecommendationResponse(userId, buildFallbackRecommendations("개인화 추천이 일시적으로 불가하여 기본 추천을 제공합니다."));
        }
    }

    @Transactional(readOnly = true)
    public AiEmotionRecommendResponse emotionRecommend(AiEmotionRecommendRequest request) {
        validateTextInput(request.emotion(), "emotion");
        if (request.context() != null && !request.context().isBlank()) {
            validateTextInput(request.context(), "context");
        }

        try {
            return new AiEmotionRecommendResponse(
                    filterRecommendableMenus(aiServerClient.emotionRecommend(request).recommendations())
            );
        } catch (Exception exception) {
            return new AiEmotionRecommendResponse(buildFallbackRecommendations("감정 기반 추천이 일시적으로 불가하여 기본 추천을 제공합니다."));
        }
    }

    @Transactional(readOnly = true)
    public AiReviewGenerateResponse generateReview(AiReviewGenerateRequest request) {
        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        validateKeywords(request.keywords());

        try {
            AiReviewGenerateResponse response = aiServerClient.generateReview(request);
            return new AiReviewGenerateResponse(response.menuId(), response.reviewDraft(), response.aiGenerated());
        } catch (Exception exception) {
            return new AiReviewGenerateResponse(
                    menu.getId(),
                    menu.getName() + "은(는) 전반적으로 만족스러웠고 다시 주문하고 싶은 메뉴였습니다.",
                    true
            );
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.REVIEW_SUMMARIES, key = "#menuId")
    public AiReviewSummaryResponse getReviewSummary(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        try {
            AiReviewSummaryResponse response = aiServerClient.getReviewSummary(menuId);
            return new AiReviewSummaryResponse(response.menuId(), response.summary());
        } catch (Exception exception) {
            return new AiReviewSummaryResponse(menu.getId(), menu.getName() + " 리뷰 요약을 준비 중입니다.");
        }
    }

    @Transactional(readOnly = true)
    public AiNewMenuRecommendationsResponse getNewMenuRecommendations() {
        try {
            return aiServerClient.getNewMenuRecommendations();
        } catch (Exception exception) {
            return new AiNewMenuRecommendationsResponse(List.of(
                    new AiNewMenuRecommendationDto(
                            "청양 제육 덮밥",
                            "KOREAN",
                            "AI 서버 호출 실패로 제공하는 기본 신메뉴 추천입니다."
                    )
            ));
        }
    }

    public AiRecommendationDto recommendLegacy(AiRecommendRequest request) {
        AiRecommendResponse response = recommend(request);
        return response.recommendations().stream()
                .findFirst()
                .orElse(new AiRecommendationDto(0L, "추천 메뉴 없음", "현재 추천 가능한 메뉴가 없습니다."));
    }

    private List<AiRecommendationDto> filterRecommendableMenus(List<AiRecommendationDto> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return buildFallbackRecommendations("기본 추천 메뉴입니다.");
        }

        List<Long> menuIds = recommendations.stream()
                .map(AiRecommendationDto::menuId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Menu> menuMap = menuRepository.findAllById(menuIds).stream()
                .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE)
                .collect(java.util.stream.Collectors.toMap(Menu::getId, Function.identity()));

        List<AiRecommendationDto> filtered = new ArrayList<>();
        for (AiRecommendationDto recommendation : recommendations) {
            Menu menu = menuMap.get(recommendation.menuId());
            if (menu == null) {
                continue;
            }

            filtered.add(new AiRecommendationDto(
                    menu.getId(),
                    menu.getName(),
                    recommendation.reason()
            ));
        }

        if (filtered.isEmpty()) {
            return buildFallbackRecommendations("기본 추천 메뉴입니다.");
        }

        return filtered;
    }

    private List<AiRecommendationDto> buildFallbackRecommendations(String reasonPrefix) {
        return menuRepository.findAll().stream()
                .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE)
                .sorted(java.util.Comparator.comparing(Menu::getId))
                .limit(3)
                .map(menu -> new AiRecommendationDto(
                        menu.getId(),
                        menu.getName(),
                        reasonPrefix
                ))
                .toList();
    }

    private void validateTextInput(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, fieldName + "는 비어 있을 수 없습니다.");
        }
    }

    private void validateKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "keywords는 최소 1개 이상이어야 합니다.");
        }

        boolean hasInvalidKeyword = keywords.stream()
                .anyMatch(keyword -> keyword == null || keyword.isBlank());

        if (hasInvalidKeyword) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비어 있는 키워드는 허용되지 않습니다.");
        }
    }
}
