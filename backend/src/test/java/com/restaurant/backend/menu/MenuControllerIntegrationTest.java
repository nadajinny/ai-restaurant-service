package com.restaurant.backend.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Long availableMenuId;
    private Long soldOutMenuId;
    private Long hiddenMenuId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();

        Menu availableMenu = menuRepository.save(Menu.create(
                "김치찌개",
                "KOREAN",
                9000,
                "진한 국물의 김치찌개",
                "https://example.com/kimchi.jpg",
                15,
                MenuStatus.AVAILABLE
        ));

        Menu soldOutMenu = menuRepository.save(Menu.create(
                "돈까스",
                "JAPANESE",
                11000,
                "바삭한 수제 돈까스",
                "https://example.com/tonkatsu.jpg",
                18,
                MenuStatus.SOLD_OUT
        ));

        Menu hiddenMenu = menuRepository.save(Menu.create(
                "비밀메뉴",
                "SPECIAL",
                15000,
                "고객에게 숨김 처리된 메뉴",
                "https://example.com/secret.jpg",
                20,
                MenuStatus.HIDDEN
        ));

        availableMenuId = availableMenu.getId();
        soldOutMenuId = soldOutMenu.getId();
        hiddenMenuId = hiddenMenu.getId();
    }

    @Test
    void getMenusReturnsVisibleMenusOnly() throws Exception {
        mockMvc.perform(get("/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("돈까스"))
                .andExpect(jsonPath("$.data[0].status").value("SOLD_OUT"))
                .andExpect(jsonPath("$.data[0].orderable").value(false))
                .andExpect(jsonPath("$.data[1].name").value("김치찌개"))
                .andExpect(jsonPath("$.data[1].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data[1].orderable").value(true));
    }

    @Test
    void getMenusByCategoryReturnsFilteredMenus() throws Exception {
        mockMvc.perform(get("/menus").param("category", "KOREAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("김치찌개"))
                .andExpect(jsonPath("$.data[0].category").value("KOREAN"));
    }

    @Test
    void getMenusByPriceRangeReturnsFilteredMenus() throws Exception {
        mockMvc.perform(get("/menus")
                        .param("minPrice", "10000")
                        .param("maxPrice", "12000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("돈까스"));
    }

    @Test
    void getMenusByStatusReturnsFilteredMenus() throws Exception {
        mockMvc.perform(get("/menus").param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("김치찌개"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    void getMenusPriceAscendingSortWorks() throws Exception {
        mockMvc.perform(get("/menus").param("sort", "PRICE_ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("김치찌개"))
                .andExpect(jsonPath("$.data[1].name").value("돈까스"));
    }

    @Test
    void getMenusPriceDescendingSortWorks() throws Exception {
        mockMvc.perform(get("/menus").param("sort", "PRICE_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("돈까스"))
                .andExpect(jsonPath("$.data[1].name").value("김치찌개"));
    }

    @Test
    void getMenusInvalidPriceRangeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/menus")
                        .param("minPrice", "15000")
                        .param("maxPrice", "10000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("minPrice는 maxPrice보다 클 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void getMenusHiddenStatusFilterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/menus").param("status", "HIDDEN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("고객용 조회에서는 HIDDEN 상태를 조회할 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void getMenusInvalidSortReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/menus").param("sort", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT_VALUE"));
    }

    @Test
    void getMenuDetailReturnsVisibleMenu() throws Exception {
        mockMvc.perform(get("/menus/{menuId}", soldOutMenuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.menuId").value(soldOutMenuId))
                .andExpect(jsonPath("$.data.name").value("돈까스"))
                .andExpect(jsonPath("$.data.description").value("바삭한 수제 돈까스"))
                .andExpect(jsonPath("$.data.status").value("SOLD_OUT"))
                .andExpect(jsonPath("$.data.orderable").value(false));
    }

    @Test
    void getMenuDetailReturnsNotFoundForHiddenMenu() throws Exception {
        mockMvc.perform(get("/menus/{menuId}", hiddenMenuId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("메뉴를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("MENU_NOT_FOUND"));
    }

    @Test
    void getMenuDetailReturnsNotFoundForUnknownMenu() throws Exception {
        mockMvc.perform(get("/menus/{menuId}", availableMenuId + soldOutMenuId + hiddenMenuId + 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("메뉴를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("MENU_NOT_FOUND"));
    }
}
