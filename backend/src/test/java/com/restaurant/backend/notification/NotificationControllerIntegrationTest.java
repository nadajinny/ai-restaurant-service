package com.restaurant.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.domain.Notification;
import com.restaurant.backend.notification.domain.NotificationType;
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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Long userId;
    private Long anotherUserId;
    private Long notificationId;

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

        User user = userRepository.save(User.create("notification-user", "password", "알림 사용자", UserRole.USER));
        User anotherUser = userRepository.save(User.create("notification-user-2", "password", "다른 사용자", UserRole.USER));

        userId = user.getId();
        anotherUserId = anotherUser.getId();

        notificationRepository.save(Notification.create(user, NotificationType.ORDER_READY, "주문한 음식의 준비가 완료되었습니다."));
        Notification latest = notificationRepository.save(Notification.create(user, NotificationType.ORDER_COOKING, "주문한 음식의 조리가 시작되었습니다."));
        notificationRepository.save(Notification.create(anotherUser, NotificationType.ORDER_CANCELED, "주문이 취소되었습니다."));
        notificationId = latest.getId();
    }

    @Test
    void getNotificationsReturnsOnlyUsersNotifications() throws Exception {
        mockMvc.perform(get("/notifications").param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].type").value("ORDER_COOKING"))
                .andExpect(jsonPath("$.data[0].read").value(false))
                .andExpect(jsonPath("$.data[1].type").value("ORDER_READY"));
    }

    @Test
    void markNotificationAsReadUpdatesReadFlag() throws Exception {
        mockMvc.perform(patch("/notifications/{notificationId}/read", notificationId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("알림이 읽음 처리되었습니다."))
                .andExpect(jsonPath("$.data.read").value(true));

        assertThat(notificationRepository.findById(notificationId).orElseThrow().isRead()).isTrue();
    }

    @Test
    void markNotificationAsReadRejectsOtherUsersNotification() throws Exception {
        Long otherNotificationId = notificationRepository.findAllByUser_IdOrderByCreatedAtDescIdDesc(anotherUserId).get(0).getId();

        mockMvc.perform(patch("/notifications/{notificationId}/read", otherNotificationId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }
}
