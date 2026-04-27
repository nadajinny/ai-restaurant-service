package com.restaurant.backend.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.coupon.repository.CouponRepository;
import com.restaurant.backend.coupon.repository.CouponUsageRepository;
import com.restaurant.backend.inventory.domain.Inventory;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.domain.Notification;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.domain.OrderStatusHistory;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.payment.repository.PaymentRepository;
import com.restaurant.backend.review.repository.ReviewRepository;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminOrderStatusIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

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

    private Long orderId;
    private Long userId;
    private Long menuId;

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

        User user = userRepository.save(User.create("order-user", "password", "주문 사용자", UserRole.USER));
        Menu menu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        inventoryRepository.save(Inventory.create(menu, 4));
        Order order = orderRepository.save(Order.create(user, 9000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(order, menu, 1, 9000));

        orderId = order.getId();
        userId = user.getId();
        menuId = menu.getId();
    }

    @Test
    void updateOrderStatusSucceedsForValidTransitionAndSavesHistoryAndNotification() throws Exception {
        mockMvc.perform(patch("/admin/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COOKING"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("주문 상태가 변경되었습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("COOKING"));

        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COOKING);
        assertThat(orderStatusHistoryRepository.countByOrder_Id(orderId)).isEqualTo(1);

        List<OrderStatusHistory> histories = orderStatusHistoryRepository.findAllByOrder_IdOrderByCreatedAtAscIdAsc(orderId);
        assertThat(histories.get(0).getFromStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(histories.get(0).getToStatus()).isEqualTo(OrderStatus.COOKING);
        assertThat(histories.get(0).getChangedBy()).isEqualTo("system-admin");

        assertThat(notificationRepository.countByUser_Id(userId)).isEqualTo(1);
        Notification notification = notificationRepository.findTopByUser_IdOrderByCreatedAtDescIdDesc(userId).orElseThrow();
        assertThat(notification.getType()).isEqualTo("ORDER_STATUS_CHANGED");
        assertThat(notification.getContent()).contains("RECEIVED").contains("COOKING");
    }

    @Test
    void updateOrderStatusRejectsInvalidTransition() throws Exception {
        mockMvc.perform(patch("/admin/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "READY"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));

        Order unchangedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(unchangedOrder.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(orderStatusHistoryRepository.countByOrder_Id(orderId)).isZero();
        assertThat(notificationRepository.countByUser_Id(userId)).isZero();
    }

    @Test
    void updateOrderStatusRejectsTransitionFromCompleted() throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.changeStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        mockMvc.perform(patch("/admin/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CANCELED"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void updateOrderStatusRejectsUnknownOrder() throws Exception {
        mockMvc.perform(patch("/admin/orders/{orderId}/status", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COOKING"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ORDER_NOT_FOUND"));
    }

    @Test
    void updateOrderStatusToCanceledRestoresInventory() throws Exception {
        mockMvc.perform(patch("/admin/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CANCELED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELED"));

        assertThat(inventoryRepository.findByMenu_Id(menuId).orElseThrow().getQuantity()).isEqualTo(5);
        assertThat(menuRepository.findById(menuId).orElseThrow().getStatus()).isEqualTo(MenuStatus.AVAILABLE);
    }
}
