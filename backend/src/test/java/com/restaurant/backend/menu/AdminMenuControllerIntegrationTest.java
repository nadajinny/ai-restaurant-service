package com.restaurant.backend.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class AdminMenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void createMenuCreatesNewMenu() throws Exception {
        mockMvc.perform(post("/admin/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "새우볶음밥",
                                  "category": "CHINESE",
                                  "price": 9500,
                                  "description": "불향이 나는 새우볶음밥",
                                  "imageUrl": "https://example.com/fried-rice.jpg",
                                  "cookingTime": 12,
                                  "status": "AVAILABLE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메뉴가 등록되었습니다."))
                .andExpect(jsonPath("$.data.name").value("새우볶음밥"))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
    }

    @Test
    void createMenuRejectsInvalidPrice() throws Exception {
        mockMvc.perform(post("/admin/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "새우볶음밥",
                                  "category": "CHINESE",
                                  "price": 0,
                                  "description": "불향이 나는 새우볶음밥",
                                  "imageUrl": "https://example.com/fried-rice.jpg",
                                  "cookingTime": 12,
                                  "status": "AVAILABLE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void updateMenuUpdatesFields() throws Exception {
        Menu menu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "기존 설명",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));

        mockMvc.perform(put("/admin/menus/{menuId}", menu.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "참치김치찌개",
                                  "category": "KOREAN",
                                  "price": 10500,
                                  "description": "참치가 들어간 김치찌개",
                                  "imageUrl": "https://example.com/tuna-kimchi.jpg",
                                  "cookingTime": 17,
                                  "status": "SOLD_OUT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("참치김치찌개"))
                .andExpect(jsonPath("$.data.price").value(10500))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"));
    }

    @Test
    void deleteMenuWithoutOrderHistoryPhysicallyDeletesMenu() throws Exception {
        Menu menu = menuRepository.save(Menu.create(
                "삭제대상",
                "KOREAN",
                8000,
                "주문 이력이 없는 메뉴",
                "https://example.com/delete.jpg",
                10,
                MenuStatus.AVAILABLE
        ));

        mockMvc.perform(delete("/admin/menus/{menuId}", menu.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메뉴가 삭제되었습니다."));

        assertThat(menuRepository.findById(menu.getId())).isEmpty();
    }

    @Test
    void deleteMenuWithOrderHistoryHidesMenuInstead() throws Exception {
        Menu menu = menuRepository.save(Menu.create(
                "숨김대상",
                "KOREAN",
                8000,
                "주문 이력이 있는 메뉴",
                "https://example.com/hide.jpg",
                10,
                MenuStatus.AVAILABLE
        ));
        User user = userRepository.save(User.create("admin-test", "password", "관리자", UserRole.ADMIN));
        Order order = orderRepository.save(Order.create(user, 8000, OrderStatus.RECEIVED));
        orderItemRepository.save(OrderItem.create(order, menu, 1, 8000));

        mockMvc.perform(delete("/admin/menus/{menuId}", menu.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Menu updatedMenu = menuRepository.findById(menu.getId()).orElseThrow();
        assertThat(updatedMenu.getStatus()).isEqualTo(MenuStatus.HIDDEN);
    }

    @Test
    void patchMenuStatusChangesStatus() throws Exception {
        Menu menu = menuRepository.save(Menu.create(
                "상태변경대상",
                "KOREAN",
                8500,
                "상태 변경 메뉴",
                "https://example.com/status.jpg",
                10,
                MenuStatus.AVAILABLE
        ));

        mockMvc.perform(patch("/admin/menus/{menuId}/status", menu.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SOLD_OUT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메뉴 상태가 변경되었습니다."))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"));
    }
}
