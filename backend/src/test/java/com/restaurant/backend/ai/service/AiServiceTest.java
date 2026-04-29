package com.restaurant.backend.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.restaurant.backend.ai.client.AiServerClient;
import com.restaurant.backend.ai.dto.AiEmotionRecommendRequest;
import com.restaurant.backend.ai.dto.AiEmotionRecommendResponse;
import com.restaurant.backend.ai.dto.AiPersonalizedRecommendationResponse;
import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendRequest;
import com.restaurant.backend.ai.dto.AiRecommendResponse;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AiServerClient aiServerClient;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private AiService aiService;

    @Test
    void recommendFiltersOutSoldOutHiddenAndMissingMenus() {
        Menu availableMenu = createMenu(1L, "김치찌개", MenuStatus.AVAILABLE);
        Menu soldOutMenu = createMenu(2L, "돈까스", MenuStatus.SOLD_OUT);
        Menu hiddenMenu = createMenu(3L, "비밀메뉴", MenuStatus.HIDDEN);

        given(aiServerClient.recommend(any(AiRecommendRequest.class))).willReturn(new AiRecommendResponse(List.of(
                new AiRecommendationDto(1L, "임시 이름", "추천 이유"),
                new AiRecommendationDto(2L, "돈까스", "품절 메뉴"),
                new AiRecommendationDto(3L, "비밀메뉴", "숨김 메뉴"),
                new AiRecommendationDto(999L, "없는 메뉴", "없는 메뉴")
        )));
        given(menuRepository.findAllById(List.of(1L, 2L, 3L, 999L))).willReturn(List.of(
                availableMenu,
                soldOutMenu,
                hiddenMenu
        ));

        AiRecommendResponse response = aiService.recommend(new AiRecommendRequest("매운 음식 추천"));

        assertThat(response.recommendations()).hasSize(1);
        assertThat(response.recommendations().get(0).menuId()).isEqualTo(1L);
        assertThat(response.recommendations().get(0).name()).isEqualTo("김치찌개");
    }

    @Test
    void personalizedRecommendationsFallbackUsesAvailableMenusOnlyWhenAiServerFails() {
        Menu availableMenu = createMenu(1L, "김치찌개", MenuStatus.AVAILABLE);
        Menu secondAvailableMenu = createMenu(4L, "제육볶음", MenuStatus.AVAILABLE);
        Menu soldOutMenu = createMenu(2L, "돈까스", MenuStatus.SOLD_OUT);

        given(aiServerClient.getPersonalizedRecommendations(eq(99L))).willThrow(new RuntimeException("AI server down"));
        given(menuRepository.findAll()).willReturn(List.of(soldOutMenu, secondAvailableMenu, availableMenu));

        AiPersonalizedRecommendationResponse response = aiService.getPersonalizedRecommendations(99L);

        assertThat(response.userId()).isEqualTo(99L);
        assertThat(response.recommendations())
                .extracting(AiRecommendationDto::menuId)
                .containsExactly(1L, 4L);
        assertThat(response.recommendations())
                .extracting(AiRecommendationDto::reason)
                .allMatch(reason -> reason.contains("개인화 추천이 일시적으로 불가"));
    }

    @Test
    void emotionRecommendFallsBackWhenAllReturnedMenusAreUnavailable() {
        Menu soldOutMenu = createMenu(2L, "돈까스", MenuStatus.SOLD_OUT);
        Menu fallbackMenu = createMenu(5L, "비빔밥", MenuStatus.AVAILABLE);

        given(aiServerClient.emotionRecommend(any(AiEmotionRecommendRequest.class))).willReturn(new AiEmotionRecommendResponse(List.of(
                new AiRecommendationDto(2L, "돈까스", "품절 메뉴")
        )));
        given(menuRepository.findAllById(List.of(2L))).willReturn(List.of(soldOutMenu));
        given(menuRepository.findAll()).willReturn(List.of(soldOutMenu, fallbackMenu));

        AiEmotionRecommendResponse response = aiService.emotionRecommend(new AiEmotionRecommendRequest("stressed", "피곤함"));

        assertThat(response.recommendations()).hasSize(1);
        assertThat(response.recommendations().get(0).menuId()).isEqualTo(5L);
        assertThat(response.recommendations().get(0).name()).isEqualTo("비빔밥");
    }

    private Menu createMenu(Long id, String name, MenuStatus status) {
        Menu menu = Menu.create(
                name,
                "KOREAN",
                9000,
                name + " 설명",
                "https://example.com/" + id + ".jpg",
                15,
                status
        );
        ReflectionTestUtils.setField(menu, "id", id);
        return menu;
    }
}
