package com.restaurant.backend.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    @Autowired
    private InventoryRepository inventoryRepository;

    private Long availableMenuId1;
    private Long availableMenuId2;
    private Long soldOutMenuId;
    private Long userId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        favoriteRepository.deleteAll();
        orderRepository.deleteAll();
        inventoryRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        userId = userRepository.save(User.create("order-api-user", "password", "주문 API 사용자", UserRole.USER)).getId();

        Menu availableMenu1 = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        availableMenuId1 = availableMenu1.getId();
        inventoryRepository.save(Inventory.create(availableMenu1, 5));

        Menu availableMenu2 = menuRepository.save(Menu.create(
                "제육볶음",
                "KOREAN",
                11000,
                "매콤한 제육볶음",
                "https://example.com/pork.jpg",
                12,
                MenuStatus.AVAILABLE
        ));
        availableMenuId2 = availableMenu2.getId();
        inventoryRepository.save(Inventory.create(availableMenu2, 1));

        Menu soldOutMenu = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                12000,
                "품절된 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.SOLD_OUT
        ));
        soldOutMenuId = soldOutMenu.getId();
        inventoryRepository.save(Inventory.create(soldOutMenu, 0));
    }

    @Test
    void createOrderSavesOrderAndOrderItems() throws Exception {
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    { "menuId": %d, "quantity": 2 },
                                    { "menuId": %d, "quantity": 1 }
                                  ],
                                  "couponCode": "WELCOME10"
                                }
                                """.formatted(availableMenuId1, availableMenuId2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("주문이 생성되었습니다."))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.totalPrice").value(29000))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number orderIdValue = com.jayway.jsonpath.JsonPath.read(response, "$.data.orderId");
        long orderId = orderIdValue.longValue();
        Order order = orderRepository.findById(orderId).orElseThrow();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(order.getTotalPrice()).isEqualTo(29000);
        assertThat(orderItemRepository.countByOrder_Id(orderId)).isEqualTo(2);
        assertThat(inventoryRepository.findByMenu_Id(availableMenuId1).orElseThrow().getQuantity()).isEqualTo(3);
        assertThat(inventoryRepository.findByMenu_Id(availableMenuId2).orElseThrow().getQuantity()).isEqualTo(0);
        assertThat(menuRepository.findById(availableMenuId2).orElseThrow().getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
    }

    @Test
    void createOrderRejectsEmptyItems() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [],
                                  "couponCode": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void createOrderRejectsInvalidQuantity() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    { "menuId": %d, "quantity": 0 }
                                  ]
                                }
                                """.formatted(availableMenuId1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void createOrderRejectsMissingMenu() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    { "menuId": 99999, "quantity": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("MENU_NOT_FOUND"));
    }

    @Test
    void createOrderRejectsNonAvailableMenu() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    { "menuId": %d, "quantity": 1 }
                                  ]
                                }
                                """.formatted(soldOutMenuId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("MENU_NOT_ORDERABLE"));
    }

    @Test
    void createOrderRejectsWhenRequestedQuantityExceedsInventory() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    { "menuId": %d, "quantity": 2 }
                                  ]
                                }
                                """.formatted(availableMenuId2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_STOCK"));
    }

    @Test
    void reorderCreatesNewOrderWithCurrentMenuPrices() throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        Menu menu1 = menuRepository.findById(availableMenuId1).orElseThrow();
        Menu menu2 = menuRepository.findById(availableMenuId2).orElseThrow();

        Order originalOrder = orderRepository.save(Order.create(user, 18000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(originalOrder, menu1, 1, 8000));
        orderItemRepository.save(OrderItem.create(originalOrder, menu2, 1, 10000));

        String response = mockMvc.perform(post("/orders/{orderId}/reorder", originalOrder.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재주문이 생성되었습니다."))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.totalPrice").value(20000))
                .andExpect(jsonPath("$.data.unavailableItems.length()").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number newOrderIdValue = com.jayway.jsonpath.JsonPath.read(response, "$.data.orderId");
        long newOrderId = newOrderIdValue.longValue();

        assertThat(newOrderId).isNotEqualTo(originalOrder.getId());
        assertThat(orderItemRepository.countByOrder_Id(originalOrder.getId())).isEqualTo(2);
        assertThat(orderItemRepository.countByOrder_Id(newOrderId)).isEqualTo(2);
    }

    @Test
    void reorderSkipsUnavailableMenusAndCreatesPartialOrder() throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        Menu availableMenu = menuRepository.findById(availableMenuId1).orElseThrow();
        Menu soldOutMenu = menuRepository.findById(soldOutMenuId).orElseThrow();

        Order originalOrder = orderRepository.save(Order.create(user, 21000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(originalOrder, availableMenu, 1, 9000));
        orderItemRepository.save(OrderItem.create(originalOrder, soldOutMenu, 1, 12000));

        mockMvc.perform(post("/orders/{orderId}/reorder", originalOrder.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.totalPrice").value(9000))
                .andExpect(jsonPath("$.data.unavailableItems.length()").value(1))
                .andExpect(jsonPath("$.data.unavailableItems[0].menuId").value(soldOutMenuId))
                .andExpect(jsonPath("$.data.unavailableItems[0].menuName").value("돈까스"));
    }

    @Test
    void reorderSupportsSelectingSubsetOfMenus() throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        Menu menu1 = menuRepository.findById(availableMenuId1).orElseThrow();
        Menu menu2 = menuRepository.findById(availableMenuId2).orElseThrow();

        Order originalOrder = orderRepository.save(Order.create(user, 20000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(originalOrder, menu1, 1, 9000));
        orderItemRepository.save(OrderItem.create(originalOrder, menu2, 1, 11000));

        mockMvc.perform(post("/orders/{orderId}/reorder", originalOrder.getId())
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuIds": [%d]
                                }
                                """.formatted(availableMenuId2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPrice").value(11000))
                .andExpect(jsonPath("$.data.unavailableItems.length()").value(0));
    }

    @Test
    void reorderFailsWhenAllMenusAreUnavailable() throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        Menu soldOutMenu = menuRepository.findById(soldOutMenuId).orElseThrow();
        soldOutMenu.changeStatus(MenuStatus.HIDDEN);
        menuRepository.save(soldOutMenu);

        Order originalOrder = orderRepository.save(Order.create(user, 12000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(originalOrder, soldOutMenu, 1, 12000));

        mockMvc.perform(post("/orders/{orderId}/reorder", originalOrder.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("REORDER_NOT_AVAILABLE"));
    }

    @Test
    void reorderRejectsOtherUsersOrder() throws Exception {
        User otherUser = userRepository.save(User.create("other-order-user", "password", "다른 사용자", UserRole.USER));
        Menu menu1 = menuRepository.findById(availableMenuId1).orElseThrow();
        Order originalOrder = orderRepository.save(Order.create(otherUser, 9000, OrderStatus.COMPLETED));
        orderItemRepository.save(OrderItem.create(originalOrder, menu1, 1, 9000));

        mockMvc.perform(post("/orders/{orderId}/reorder", originalOrder.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }
}
