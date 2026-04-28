package com.restaurant.backend.analytics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
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
import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.domain.ReviewStatus;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin01", roles = "ADMIN")
class AdminAnalyticsIntegrationTest {

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
    }

    @Test
    void dashboardAndAnalyticsReturnAggregatedData() throws Exception {
        User user = userRepository.save(User.create("analytics-user", "password", "분석 사용자", UserRole.USER));

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
                "돈까스",
                "JAPANESE",
                12000,
                "바삭한 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.AVAILABLE
        ));
        Menu menu3 = menuRepository.save(Menu.create(
                "라면",
                "KOREAN",
                8000,
                "품절된 라면",
                "https://example.com/ramen.jpg",
                8,
                MenuStatus.SOLD_OUT
        ));

        LocalDateTime now = LocalDateTime.now();

        Order completedOrder1 = orderRepository.save(Order.create(user, 18000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(completedOrder1, menu1, 2, 9000));
        setCreatedAt(completedOrder1, now.withHour(10).withMinute(0).withSecond(0).withNano(0));
        orderRepository.save(completedOrder1);

        Order completedOrder2 = orderRepository.save(Order.create(user, 12000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(completedOrder2, menu2, 1, 12000));
        setCreatedAt(completedOrder2, now.withHour(12).withMinute(0).withSecond(0).withNano(0));
        orderRepository.save(completedOrder2);

        Order receivedOrder = orderRepository.save(Order.create(user, 9000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(receivedOrder, menu1, 1, 9000));
        setCreatedAt(receivedOrder, now.withHour(15).withMinute(0).withSecond(0).withNano(0));
        orderRepository.save(receivedOrder);

        Order canceledOrder = orderRepository.save(Order.create(user, 8000, OrderStatus.CANCELED));
        orderItemRepository.save(OrderItem.create(canceledOrder, menu3, 1, 8000));
        setCreatedAt(canceledOrder, now.minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
        orderRepository.save(canceledOrder);

        Review recentReview1 = reviewRepository.save(Review.create(
                user,
                menu2,
                completedOrder2,
                "돈까스가 바삭했습니다.",
                5,
                false,
                ReviewStatus.ACTIVE
        ));
        setCreatedAt(recentReview1, now.withHour(13).withMinute(0).withSecond(0).withNano(0));
        reviewRepository.save(recentReview1);

        Review recentReview2 = reviewRepository.save(Review.create(
                user,
                menu1,
                completedOrder1,
                "김치찌개가 맛있었습니다.",
                4,
                false,
                ReviewStatus.ACTIVE
        ));
        setCreatedAt(recentReview2, now.withHour(11).withMinute(0).withSecond(0).withNano(0));
        reviewRepository.save(recentReview2);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.todayOrderCount").value(3))
                .andExpect(jsonPath("$.data.todaySales").value(30000))
                .andExpect(jsonPath("$.data.popularMenus[0].menuName").value("김치찌개"))
                .andExpect(jsonPath("$.data.popularMenus[0].soldQuantity").value(2))
                .andExpect(jsonPath("$.data.hourlyOrders[10].orderCount").value(1))
                .andExpect(jsonPath("$.data.hourlyOrders[12].orderCount").value(1))
                .andExpect(jsonPath("$.data.hourlyOrders[15].orderCount").value(1))
                .andExpect(jsonPath("$.data.soldOutMenus.length()").value(1))
                .andExpect(jsonPath("$.data.soldOutMenus[0].menuName").value("라면"))
                .andExpect(jsonPath("$.data.recentReviews[0].menuName").value("돈까스"))
                .andExpect(jsonPath("$.data.recentReviews[1].menuName").value("김치찌개"));

        mockMvc.perform(get("/admin/analytics/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completedOrderCount").value(2))
                .andExpect(jsonPath("$.data.totalSales").value(30000))
                .andExpect(jsonPath("$.data.averageOrderAmount").value(15000));

        mockMvc.perform(get("/admin/analytics/popular-menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].menuName").value("김치찌개"))
                .andExpect(jsonPath("$.data[0].soldQuantity").value(2))
                .andExpect(jsonPath("$.data[0].salesAmount").value(18000))
                .andExpect(jsonPath("$.data[1].menuName").value("돈까스"))
                .andExpect(jsonPath("$.data[1].soldQuantity").value(1))
                .andExpect(jsonPath("$.data[1].salesAmount").value(12000));

        mockMvc.perform(get("/admin/analytics/menu-performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].menuName").value("김치찌개"))
                .andExpect(jsonPath("$.data[0].soldQuantity").value(2))
                .andExpect(jsonPath("$.data[1].menuName").value("돈까스"))
                .andExpect(jsonPath("$.data[1].soldQuantity").value(1))
                .andExpect(jsonPath("$.data[2].menuName").value("라면"))
                .andExpect(jsonPath("$.data[2].soldQuantity").value(0))
                .andExpect(jsonPath("$.data[2].salesAmount").value(0));

        mockMvc.perform(get("/admin/analytics/hourly-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(24))
                .andExpect(jsonPath("$.data[9].orderCount").value(1))
                .andExpect(jsonPath("$.data[10].orderCount").value(1))
                .andExpect(jsonPath("$.data[12].orderCount").value(1))
                .andExpect(jsonPath("$.data[15].orderCount").value(1));
    }

    @Test
    void dashboardAndAnalyticsReturnZerosAndEmptyListsWhenNoDataExists() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todayOrderCount").value(0))
                .andExpect(jsonPath("$.data.todaySales").value(0))
                .andExpect(jsonPath("$.data.popularMenus.length()").value(0))
                .andExpect(jsonPath("$.data.soldOutMenus.length()").value(0))
                .andExpect(jsonPath("$.data.recentReviews.length()").value(0));

        mockMvc.perform(get("/admin/analytics/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completedOrderCount").value(0))
                .andExpect(jsonPath("$.data.totalSales").value(0))
                .andExpect(jsonPath("$.data.averageOrderAmount").value(0));

        mockMvc.perform(get("/admin/analytics/popular-menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        mockMvc.perform(get("/admin/analytics/menu-performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        mockMvc.perform(get("/admin/analytics/hourly-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(24))
                .andExpect(jsonPath("$.data[0].orderCount").value(0))
                .andExpect(jsonPath("$.data[23].orderCount").value(0));
    }

    private void setCreatedAt(Object target, LocalDateTime createdAt) {
        ReflectionTestUtils.setField(target, "createdAt", createdAt);
        ReflectionTestUtils.setField(target, "updatedAt", createdAt);
    }
}
