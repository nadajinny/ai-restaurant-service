package com.restaurant.backend.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.coupon.domain.Coupon;
import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.inventory.domain.Inventory;
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
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Long userId;
    private Long anotherUserId;
    private Long orderId;
    private Long anotherOrderId;

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

        User user = userRepository.save(User.create("coupon-user", "password", "쿠폰 사용자", UserRole.USER));
        User anotherUser = userRepository.save(User.create("coupon-user-2", "password", "다른 사용자", UserRole.USER));
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
        inventoryRepository.save(Inventory.create(menu, 10));

        Order order = orderRepository.save(Order.create(user, 18000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(order, menu, 2, 9000));
        orderId = order.getId();

        Order anotherOrder = orderRepository.save(Order.create(anotherUser, 18000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(anotherOrder, menu, 2, 9000));
        anotherOrderId = anotherOrder.getId();
    }

    @Test
    @WithMockUser(username = "admin01", roles = "ADMIN")
    void adminCreatesUpdatesAndDisablesCoupon() throws Exception {
        String response = mockMvc.perform(post("/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "WELCOME10",
                                  "name": "웰컴 10퍼센트",
                                  "discountAmount": null,
                                  "discountRate": 10,
                                  "maxDiscountAmount": 3000,
                                  "minOrderAmount": 10000,
                                  "availableFrom": "2026-04-01T00:00:00",
                                  "availableTo": "2026-05-31T23:59:59",
                                  "availableCount": 100,
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("WELCOME10"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number couponIdValue = com.jayway.jsonpath.JsonPath.read(response, "$.data.couponId");
        long couponId = couponIdValue.longValue();

        mockMvc.perform(put("/admin/coupons/{couponId}", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "WELCOME10",
                                  "name": "웰컴 2000원",
                                  "discountAmount": 2000,
                                  "discountRate": null,
                                  "maxDiscountAmount": null,
                                  "minOrderAmount": 12000,
                                  "availableFrom": "2026-04-01T00:00:00",
                                  "availableTo": "2026-05-31T23:59:59",
                                  "availableCount": 50,
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("웰컴 2000원"))
                .andExpect(jsonPath("$.data.discountAmount").value(2000));

        mockMvc.perform(patch("/admin/coupons/{couponId}/disable", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    void getAvailableCouponsReturnsActiveAndUsableCoupons() throws Exception {
        couponRepository.save(Coupon.create(
                "ACTIVE10",
                "활성 쿠폰",
                null,
                10,
                3000,
                10000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                3,
                true
        ));
        couponRepository.save(Coupon.create(
                "INACTIVE10",
                "비활성 쿠폰",
                1000,
                null,
                null,
                5000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                3,
                false
        ));

        mockMvc.perform(get("/coupons/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("ACTIVE10"));
    }

    @Test
    @WithMockUser(username = "coupon-user", roles = "USER")
    void applyCouponCalculatesDiscountAndStoresUsage() throws Exception {
        couponRepository.save(Coupon.create(
                "WELCOME10",
                "웰컴 10퍼센트",
                null,
                10,
                3000,
                10000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                5,
                true
        ));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "WELCOME10"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("쿠폰이 적용되었습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.couponCode").value("WELCOME10"))
                .andExpect(jsonPath("$.data.originalTotalPrice").value(18000))
                .andExpect(jsonPath("$.data.discountAmount").value(1800))
                .andExpect(jsonPath("$.data.finalTotalPrice").value(16200));

        assertThat(orderRepository.findById(orderId).orElseThrow().getTotalPrice()).isEqualTo(16200);
        assertThat(couponUsageRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "coupon-user", roles = "USER")
    void applyCouponRejectsDuplicateUsageBySameUser() throws Exception {
        Coupon coupon = couponRepository.save(Coupon.create(
                "ONE-TIME",
                "1회 쿠폰",
                2000,
                null,
                null,
                10000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                5,
                true
        ));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "ONE-TIME"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk());

        Order secondOrder = orderRepository.save(Order.create(
                userRepository.findById(userId).orElseThrow(),
                18000,
                OrderStatus.RECEIVED
        ));
        Menu menu = menuRepository.findAll().get(0);
        orderItemRepository.save(OrderItem.create(secondOrder, menu, 2, 9000));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "ONE-TIME"
                                }
                                """.formatted(secondOrder.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));

        assertThat(couponUsageRepository.countByCoupon_Id(coupon.getId())).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "coupon-user", roles = "USER")
    void applyCouponRejectsOtherUsersOrderAndMinOrderViolation() throws Exception {
        couponRepository.save(Coupon.create(
                "MIN25000",
                "최소 주문 금액 쿠폰",
                3000,
                null,
                null,
                25000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                5,
                true
        ));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "MIN25000"
                                }
                                """.formatted(anotherOrderId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "MIN25000"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    @WithMockUser(username = "coupon-user", roles = "USER")
    void applyCouponRejectsInactiveAndExpiredCoupon() throws Exception {
        couponRepository.save(Coupon.create(
                "INACTIVE10",
                "비활성 쿠폰",
                1000,
                null,
                null,
                10000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                5,
                false
        ));
        couponRepository.save(Coupon.create(
                "EXPIRED10",
                "만료 쿠폰",
                1000,
                null,
                null,
                10000,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(1),
                5,
                true
        ));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "INACTIVE10"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "EXPIRED10"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    @WithMockUser(username = "coupon-user", roles = "USER")
    void applyCouponRejectsWhenAvailableCountIsExhausted() throws Exception {
        Coupon exhaustedCoupon = couponRepository.save(Coupon.create(
                "LIMIT1",
                "1회 한정 쿠폰",
                1000,
                null,
                null,
                10000,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1,
                true
        ));

        couponUsageRepository.save(com.restaurant.backend.coupon.domain.CouponUsage.create(
                exhaustedCoupon,
                userRepository.findById(anotherUserId).orElseThrow(),
                orderRepository.findById(anotherOrderId).orElseThrow(),
                1000
        ));

        mockMvc.perform(post("/coupons/apply")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "couponCode": "LIMIT1"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }
}
