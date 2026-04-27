package com.restaurant.backend.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.restaurant.backend.payment.domain.Payment;
import com.restaurant.backend.payment.domain.PaymentStatus;
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
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Long orderId;
    private Long menuId;
    private Long userId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        favoriteRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        User user = userRepository.save(User.create("payment-user", "password", "결제 사용자", UserRole.USER));
        Menu menu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        inventoryRepository.save(Inventory.create(menu, 3));

        Order order = orderRepository.save(Order.create(user, 18000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(order, menu, 2, 9000));

        orderId = order.getId();
        menuId = menu.getId();
        userId = user.getId();
    }

    @Test
    void createPaymentApprovesMockPayment() throws Exception {
        String response = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "mockResult": "APPROVED"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("결제가 처리되었습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.amount").value(18000))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number paymentIdValue = com.jayway.jsonpath.JsonPath.read(response, "$.data.paymentId");
        long paymentId = paymentIdValue.longValue();
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();

        assertThat(payment.getAmount()).isEqualTo(18000);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(inventoryRepository.findByMenu_Id(menuId).orElseThrow().getQuantity()).isEqualTo(3);
    }

    @Test
    void createPaymentFailureCancelsOrderAndRestoresInventory() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "mockResult": "FAILED"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("FAILED"));

        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(inventoryRepository.findByMenu_Id(menuId).orElseThrow().getQuantity()).isEqualTo(5);
        assertThat(orderStatusHistoryRepository.countByOrder_Id(orderId)).isEqualTo(1);
        assertThat(notificationRepository.countByUser_Id(userId)).isEqualTo(1);
    }

    @Test
    void getPaymentReturnsPaymentDetail() throws Exception {
        Payment payment = paymentRepository.save(Payment.create(
                orderRepository.findById(orderId).orElseThrow(),
                18000,
                PaymentStatus.APPROVED
        ));

        mockMvc.perform(get("/payments/{paymentId}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value(payment.getId()))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.amount").value(18000))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    void cancelPaymentCancelsApprovedPaymentAndOrder() throws Exception {
        Payment payment = paymentRepository.save(Payment.create(
                orderRepository.findById(orderId).orElseThrow(),
                18000,
                PaymentStatus.APPROVED
        ));

        mockMvc.perform(post("/payments/{paymentId}/cancel", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("결제가 취소되었습니다."))
                .andExpect(jsonPath("$.data.status").value("CANCELED"));

        assertThat(paymentRepository.findById(payment.getId()).orElseThrow().getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(orderRepository.findById(orderId).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(inventoryRepository.findByMenu_Id(menuId).orElseThrow().getQuantity()).isEqualTo(5);
        assertThat(orderStatusHistoryRepository.countByOrder_Id(orderId)).isEqualTo(1);
    }

    @Test
    void createPaymentRejectsDuplicatePaymentForOrder() throws Exception {
        paymentRepository.save(Payment.create(
                orderRepository.findById(orderId).orElseThrow(),
                18000,
                PaymentStatus.APPROVED
        ));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d
                                }
                                """.formatted(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }
}
