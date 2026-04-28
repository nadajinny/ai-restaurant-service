package com.restaurant.backend.ai;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.ai.client.AiServerClient;
import com.restaurant.backend.ai.dto.AiEmotionRecommendResponse;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationDto;
import com.restaurant.backend.ai.dto.AiNewMenuRecommendationsResponse;
import com.restaurant.backend.ai.dto.AiPersonalizedRecommendationResponse;
import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendResponse;
import com.restaurant.backend.ai.dto.AiReviewGenerateResponse;
import com.restaurant.backend.ai.dto.AiReviewSummaryResponse;
import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.payment.repository.PaymentRepository;
import com.restaurant.backend.review.repository.ReviewRepository;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private AiServerClient aiServerClient;

    private Long availableMenuId;
    private Long soldOutMenuId;
    private Long hiddenMenuId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        favoriteRepository.deleteAll();
        couponUsageRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        availableMenuId = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        )).getId();

        soldOutMenuId = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                12000,
                "품절된 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.SOLD_OUT
        )).getId();

        hiddenMenuId = menuRepository.save(Menu.create(
                "비밀메뉴",
                "SPECIAL",
                15000,
                "숨김 메뉴",
                "https://example.com/secret.jpg",
                20,
                MenuStatus.HIDDEN
        )).getId();
    }

    @Test
    void recommendFiltersMissingAndUnavailableMenus() throws Exception {
        given(aiServerClient.recommend(any())).willReturn(new AiRecommendResponse(List.of(
                new AiRecommendationDto(availableMenuId, "잘못된 이름", "추천 이유 1"),
                new AiRecommendationDto(soldOutMenuId, "돈까스", "추천 이유 2"),
                new AiRecommendationDto(hiddenMenuId, "비밀메뉴", "추천 이유 3"),
                new AiRecommendationDto(99999L, "없는 메뉴", "추천 이유 4")
        )));

        mockMvc.perform(post("/ai/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "매운 음식 추천해줘"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recommendations.length()").value(1))
                .andExpect(jsonPath("$.data.recommendations[0].menuId").value(availableMenuId))
                .andExpect(jsonPath("$.data.recommendations[0].name").value("김치찌개"));
    }

    @Test
    void personalizedRecommendationsReturnsFallbackWhenAiServerFails() throws Exception {
        given(aiServerClient.getPersonalizedRecommendations(eq(1L))).willThrow(new RuntimeException("AI server down"));

        mockMvc.perform(get("/ai/personalized-recommendations").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.recommendations.length()").value(1))
                .andExpect(jsonPath("$.data.recommendations[0].menuId").value(availableMenuId));
    }

    @Test
    void emotionRecommendUsesFilteredResults() throws Exception {
        given(aiServerClient.emotionRecommend(any())).willReturn(new AiEmotionRecommendResponse(List.of(
                new AiRecommendationDto(availableMenuId, "김치찌개", "감정 추천 이유"),
                new AiRecommendationDto(soldOutMenuId, "돈까스", "제외되어야 함")
        )));

        mockMvc.perform(post("/ai/emotion-recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emotion": "stressed",
                                  "context": "오늘 일이 많았어"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recommendations.length()").value(1))
                .andExpect(jsonPath("$.data.recommendations[0].menuId").value(availableMenuId));
    }

    @Test
    void reviewGenerateReturnsFallbackWhenAiServerFails() throws Exception {
        given(aiServerClient.generateReview(any())).willThrow(new RuntimeException("AI server down"));

        mockMvc.perform(post("/ai/review-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d,
                                  "keywords": ["맛있다", "친절하다"]
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuId").value(availableMenuId))
                .andExpect(jsonPath("$.data.aiGenerated").value(true));
    }

    @Test
    void reviewSummaryReturnsFallbackWhenAiServerFails() throws Exception {
        given(aiServerClient.getReviewSummary(eq(availableMenuId))).willThrow(new RuntimeException("AI server down"));

        mockMvc.perform(get("/ai/menus/{menuId}/review-summary", availableMenuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuId").value(availableMenuId))
                .andExpect(jsonPath("$.data.summary").value("김치찌개 리뷰 요약을 준비 중입니다."));
    }

    @Test
    void newMenuRecommendationsReturnsAiServerResponse() throws Exception {
        given(aiServerClient.getNewMenuRecommendations()).willReturn(new AiNewMenuRecommendationsResponse(List.of(
                new AiNewMenuRecommendationDto("청양 제육 덮밥", "KOREAN", "수요가 높습니다.")
        )));

        mockMvc.perform(get("/admin/ai/new-menu-recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recommendations.length()").value(1))
                .andExpect(jsonPath("$.data.recommendations[0].name").value("청양 제육 덮밥"));
    }
}
