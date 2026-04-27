package com.restaurant.backend.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.inventory.domain.Inventory;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminInventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MenuRepository menuRepository;

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
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Long menuId;
    private Long soldOutMenuId;

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

        Menu menu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));
        inventoryRepository.save(Inventory.create(menu, 7));
        menuId = menu.getId();

        Menu soldOutMenu = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                12000,
                "바삭한 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.SOLD_OUT
        ));
        inventoryRepository.save(Inventory.create(soldOutMenu, 0));
        soldOutMenuId = soldOutMenu.getId();
    }

    @Test
    void getInventoriesReturnsMenuInventoryList() throws Exception {
        mockMvc.perform(get("/admin/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].menuId").value(menuId))
                .andExpect(jsonPath("$.data[0].menuName").value("김치찌개"))
                .andExpect(jsonPath("$.data[0].quantity").value(7))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    void updateInventoryChangesQuantityAndAutoSoldOut() throws Exception {
        mockMvc.perform(put("/admin/inventories/{menuId}", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("재고가 수정되었습니다."))
                .andExpect(jsonPath("$.data.quantity").value(0))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"));

        assertThat(inventoryRepository.findByMenu_Id(menuId).orElseThrow().getQuantity()).isZero();
        assertThat(menuRepository.findById(menuId).orElseThrow().getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
    }

    @Test
    void patchSoldOutMarksMenuAsSoldOut() throws Exception {
        mockMvc.perform(patch("/admin/inventories/{menuId}/sold-out", menuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메뉴가 품절 처리되었습니다."))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"));

        assertThat(menuRepository.findById(menuId).orElseThrow().getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
    }

    @Test
    void patchAvailableRestoresAvailableWhenQuantityExists() throws Exception {
        mockMvc.perform(put("/admin/inventories/{menuId}", soldOutMenuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 3
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/admin/inventories/{menuId}/available", soldOutMenuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메뉴가 판매 가능 상태로 변경되었습니다."))
                .andExpect(jsonPath("$.data.quantity").value(3))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
    }

    @Test
    void patchAvailableRejectsZeroQuantity() throws Exception {
        mockMvc.perform(patch("/admin/inventories/{menuId}/available", soldOutMenuId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }
}
