package com.restaurant.backend.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.payment.repository.PaymentRepository;
import com.restaurant.backend.review.repository.ReviewRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
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
class OrderQueryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Long userId;
    private Long otherUserId;
    private Long latestOrderId;
    private Long otherUserOrderId;

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

        User user = userRepository.save(User.create("user-a", "password", "사용자A", UserRole.USER));
        User otherUser = userRepository.save(User.create("user-b", "password", "사용자B", UserRole.USER));

        Menu menu1 = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        Menu menu2 = menuRepository.save(Menu.create(
                "제육볶음",
                "KOREAN",
                11000,
                "매콤한 제육볶음",
                "https://example.com/pork.jpg",
                12,
                MenuStatus.AVAILABLE
        ));
        Menu menu3 = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                12000,
                "바삭한 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.AVAILABLE
        ));

        Order olderOrder = orderRepository.save(Order.create(user, 9000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(olderOrder, menu1, 1, 9000));

        Order latestOrder = orderRepository.save(Order.create(user, 31000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(latestOrder, menu2, 1, 11000));
        orderItemRepository.save(OrderItem.create(latestOrder, menu3, 1, 12000));
        orderItemRepository.save(OrderItem.create(latestOrder, menu1, 1, 8000));

        Order otherOrder = orderRepository.save(Order.create(otherUser, 12000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(otherOrder, menu3, 1, 12000));

        userId = user.getId();
        otherUserId = otherUser.getId();
        latestOrderId = latestOrder.getId();
        otherUserOrderId = otherOrder.getId();
    }

    @Test
    void getOrdersReturnsOnlyCurrentUsersOrdersInLatestOrder() throws Exception {
        mockMvc.perform(get("/orders").param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].orderId").value(latestOrderId))
                .andExpect(jsonPath("$.data[0].status").value("RECEIVED"))
                .andExpect(jsonPath("$.data[0].totalPrice").value(31000))
                .andExpect(jsonPath("$.data[0].representativeMenuName").value("제육볶음"))
                .andExpect(jsonPath("$.data[1].status").value("COMPLETED"));
    }

    @Test
    void getOrderReturnsDetailsForOwnersOrder() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", latestOrderId).param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(latestOrderId))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.totalPrice").value(31000))
                .andExpect(jsonPath("$.data.items.length()").value(3))
                .andExpect(jsonPath("$.data.items[0].menuName").value("제육볶음"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(1))
                .andExpect(jsonPath("$.data.items[0].itemPrice").value(11000));
    }

    @Test
    void getOrderRejectsOtherUsersOrder() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", otherUserOrderId).param("userId", String.valueOf(userId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("다른 사용자의 주문은 조회할 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    @Test
    void getOrdersRejectsUnknownUser() throws Exception {
        mockMvc.perform(get("/orders").param("userId", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }

    @Test
    void getOrderRejectsUnknownOrder() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", "99999").param("userId", String.valueOf(otherUserId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ORDER_NOT_FOUND"));
    }
}
