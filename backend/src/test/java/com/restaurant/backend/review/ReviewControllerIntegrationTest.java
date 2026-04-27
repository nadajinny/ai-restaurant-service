package com.restaurant.backend.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.domain.ReviewStatus;
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
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Long userId;
    private Long anotherUserId;
    private Long menuId;
    private Long anotherMenuId;
    private Long orderId;
    private Long anotherUserOrderId;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        User user = userRepository.save(User.create("review-user", "password", "리뷰 사용자", UserRole.USER));
        User anotherUser = userRepository.save(User.create("review-user-2", "password", "다른 사용자", UserRole.USER));

        userId = user.getId();
        anotherUserId = anotherUser.getId();

        Menu menu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        Menu anotherMenu = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                12000,
                "바삭한 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.AVAILABLE
        ));

        menuId = menu.getId();
        anotherMenuId = anotherMenu.getId();

        Order order = orderRepository.save(Order.create(user, 9000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(order, menu, 1, 9000));
        orderId = order.getId();

        Order anotherOrder = orderRepository.save(Order.create(anotherUser, 9000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(anotherOrder, menu, 1, 9000));
        anotherUserOrderId = anotherOrder.getId();
    }

    @Test
    void createReviewSucceedsWhenUserOrderedMenu() throws Exception {
        mockMvc.perform(post("/reviews")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "menuId": %d,
                                  "content": "맛있고 양도 충분했습니다.",
                                  "rating": 5,
                                  "aiGenerated": true
                                }
                                """.formatted(orderId, menuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 작성되었습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.menuId").value(menuId))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.aiGenerated").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        assertThat(reviewRepository.findAll()).hasSize(1);
    }

    @Test
    void createReviewRejectsDuplicateReviewForSameOrderMenu() throws Exception {
        reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "이미 작성한 리뷰",
                4,
                false,
                ReviewStatus.ACTIVE
        ));

        mockMvc.perform(post("/reviews")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "menuId": %d,
                                  "content": "중복 리뷰",
                                  "rating": 5,
                                  "aiGenerated": false
                                }
                                """.formatted(orderId, menuId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void createReviewRejectsMenuNotIncludedInOrder() throws Exception {
        mockMvc.perform(post("/reviews")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "menuId": %d,
                                  "content": "주문하지 않은 메뉴 리뷰",
                                  "rating": 3,
                                  "aiGenerated": false
                                }
                                """.formatted(orderId, anotherMenuId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void createReviewRejectsOtherUsersOrder() throws Exception {
        mockMvc.perform(post("/reviews")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "menuId": %d,
                                  "content": "다른 사람 주문 리뷰",
                                  "rating": 3,
                                  "aiGenerated": false
                                }
                                """.formatted(anotherUserOrderId, menuId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    @Test
    void getMenuReviewsReturnsOnlyActiveReviews() throws Exception {
        reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "노출되는 리뷰",
                5,
                false,
                ReviewStatus.ACTIVE
        ));
        reviewRepository.save(Review.create(
                userRepository.findById(anotherUserId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(anotherUserOrderId).orElseThrow(),
                "숨김 리뷰",
                2,
                false,
                ReviewStatus.HIDDEN
        ));
        reviewRepository.save(Review.create(
                userRepository.findById(anotherUserId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(anotherUserOrderId).orElseThrow(),
                "삭제 리뷰",
                1,
                true,
                ReviewStatus.DELETED
        ));

        mockMvc.perform(get("/menus/{menuId}/reviews", menuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].content").value("노출되는 리뷰"))
                .andExpect(jsonPath("$.data[0].rating").value(5));
    }

    @Test
    void updateReviewAllowsOwnerOnly() throws Exception {
        Review review = reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "수정 전 리뷰",
                4,
                false,
                ReviewStatus.ACTIVE
        ));

        mockMvc.perform(put("/reviews/{reviewId}", review.getId())
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "수정 후 리뷰",
                                  "rating": 5,
                                  "aiGenerated": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 수정되었습니다."))
                .andExpect(jsonPath("$.data.content").value("수정 후 리뷰"))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.aiGenerated").value(true));

        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(updatedReview.getContent()).isEqualTo("수정 후 리뷰");
        assertThat(updatedReview.getRating()).isEqualTo(5);
        assertThat(updatedReview.isAiGenerated()).isTrue();
    }

    @Test
    void deleteReviewMarksStatusAsDeleted() throws Exception {
        Review review = reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "삭제할 리뷰",
                4,
                false,
                ReviewStatus.ACTIVE
        ));

        mockMvc.perform(delete("/reviews/{reviewId}", review.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 삭제되었습니다."));

        Review deletedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(deletedReview.getStatus()).isEqualTo(ReviewStatus.DELETED);
    }

    @Test
    void adminReviewsReturnsAllStatuses() throws Exception {
        reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "활성 리뷰",
                5,
                false,
                ReviewStatus.ACTIVE
        ));
        reviewRepository.save(Review.create(
                userRepository.findById(anotherUserId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(anotherUserOrderId).orElseThrow(),
                "삭제 리뷰",
                1,
                true,
                ReviewStatus.DELETED
        ));

        mockMvc.perform(get("/admin/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void adminHideReviewChangesStatusToHidden() throws Exception {
        Review review = reviewRepository.save(Review.create(
                userRepository.findById(userId).orElseThrow(),
                menuRepository.findById(menuId).orElseThrow(),
                orderRepository.findById(orderId).orElseThrow(),
                "숨김 처리할 리뷰",
                2,
                false,
                ReviewStatus.ACTIVE
        ));

        mockMvc.perform(patch("/admin/reviews/{reviewId}/hide", review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 숨김 처리되었습니다."))
                .andExpect(jsonPath("$.data.status").value("HIDDEN"));

        Review hiddenReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(hiddenReview.getStatus()).isEqualTo(ReviewStatus.HIDDEN);
    }
}
