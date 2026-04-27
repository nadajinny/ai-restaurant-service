package com.restaurant.backend.favorite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
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
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FavoriteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Long userId;
    private Long availableMenuId;
    private Long soldOutMenuId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();
        favoriteRepository.deleteAll();
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        couponUsageRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        userId = userRepository.save(User.create("favorite-user", "password", "즐겨찾기 사용자", UserRole.USER)).getId();

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
                "바삭한 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.SOLD_OUT
        )).getId();
    }

    @Test
    void createFavoriteAddsFavorite() throws Exception {
        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("즐겨찾기가 추가되었습니다."))
                .andExpect(jsonPath("$.data.menuId").value(availableMenuId))
                .andExpect(jsonPath("$.data.name").value("김치찌개"))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));

        assertThat(favoriteRepository.findAll()).hasSize(1);
    }

    @Test
    void createFavoriteDoesNotDuplicateSameUserAndMenu() throws Exception {
        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(favoriteRepository.findAll()).hasSize(1);
    }

    @Test
    void getFavoritesReturnsFavoriteList() throws Exception {
        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(soldOutMenuId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/favorites").param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].menuId").value(soldOutMenuId))
                .andExpect(jsonPath("$.data[0].name").value("돈까스"))
                .andExpect(jsonPath("$.data[0].price").value(12000))
                .andExpect(jsonPath("$.data[0].imageUrl").value("https://example.com/tonkatsu.jpg"))
                .andExpect(jsonPath("$.data[0].category").value("JAPANESE"))
                .andExpect(jsonPath("$.data[0].status").value("SOLD_OUT"));
    }

    @Test
    void deleteFavoriteRemovesFavorite() throws Exception {
        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": %d
                                }
                                """.formatted(availableMenuId)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/favorites/{menuId}", availableMenuId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("즐겨찾기가 해제되었습니다."));

        assertThat(favoriteRepository.findAll()).isEmpty();
    }

    @Test
    void createFavoriteRejectsUnknownMenu() throws Exception {
        mockMvc.perform(post("/favorites")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuId": 99999
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("MENU_NOT_FOUND"));
    }

    @Test
    void deleteFavoriteRejectsUnknownFavorite() throws Exception {
        mockMvc.perform(delete("/favorites/{menuId}", availableMenuId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("FAVORITE_NOT_FOUND"));
    }
}
