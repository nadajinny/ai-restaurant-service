package com.restaurant.backend.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.restaurant.backend.common.cache.CacheInvalidationService;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.inventory.service.InventoryService;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.notification.service.NotificationService;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.dto.OrderCreateItemRequest;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.dto.ReorderResponse;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CacheInvalidationService cacheInvalidationService;

    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                menuRepository,
                userRepository,
                orderMapper,
                inventoryService,
                notificationService,
                cacheInvalidationService
        );
    }

    @Test
    void createOrderCalculatesTotalPriceFromCurrentMenuPrices() {
        Long userId = 1L;
        User user = createUser(userId, "order-user");
        Menu kimchi = createMenu(10L, "김치찌개", 9000, MenuStatus.AVAILABLE);
        Menu pork = createMenu(20L, "제육볶음", 11000, MenuStatus.AVAILABLE);
        OrderCreateRequest request = new OrderCreateRequest(List.of(
                new OrderCreateItemRequest(10L, 2),
                new OrderCreateItemRequest(20L, 1)
        ), null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(menuRepository.findAllById(List.of(10L, 20L))).willReturn(List.of(kimchi, pork));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 100L);
            return order;
        });

        OrderCreateResponse response = orderService.createOrder(userId, request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualTo(29000);
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.RECEIVED);

        ArgumentCaptor<List<OrderItem>> orderItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository).saveAll(orderItemsCaptor.capture());
        assertThat(orderItemsCaptor.getValue())
                .extracting(OrderItem::getItemPrice)
                .containsExactly(9000, 11000);

        assertThat(response.orderId()).isEqualTo(100L);
        assertThat(response.totalPrice()).isEqualTo(29000);
        verify(inventoryService).reserveOrderInventory(eq(request.items()), any());
        verify(notificationService).createOrderReceivedNotification(orderCaptor.getValue());
    }

    @Test
    void createOrderRejectsSoldOutMenu() {
        Long userId = 1L;
        Menu soldOutMenu = createMenu(10L, "돈까스", 12000, MenuStatus.SOLD_OUT);
        OrderCreateRequest request = new OrderCreateRequest(List.of(
                new OrderCreateItemRequest(10L, 1)
        ), null);

        given(menuRepository.findAllById(List.of(10L))).willReturn(List.of(soldOutMenu));

        assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.MENU_NOT_ORDERABLE));
    }

    @Test
    void createOrderRejectsWhenInventoryIsInsufficient() {
        Long userId = 1L;
        User user = createUser(userId, "order-user");
        Menu menu = createMenu(10L, "제육볶음", 11000, MenuStatus.AVAILABLE);
        OrderCreateRequest request = new OrderCreateRequest(List.of(
                new OrderCreateItemRequest(10L, 3)
        ), null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(menuRepository.findAllById(List.of(10L))).willReturn(List.of(menu));
        doThrow(new BusinessException(ErrorCode.INSUFFICIENT_STOCK))
                .when(inventoryService).reserveOrderInventory(eq(request.items()), any());

        assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.INSUFFICIENT_STOCK));
    }

    @Test
    void reorderRecalculatesPriceUsingCurrentMenuPrice() {
        Long userId = 1L;
        User user = createUser(userId, "order-user");
        Menu currentKimchi = createMenu(10L, "김치찌개", 9000, MenuStatus.AVAILABLE);
        Menu currentPork = createMenu(20L, "제육볶음", 11000, MenuStatus.AVAILABLE);

        Order originalOrder = Order.create(user, 18000, OrderStatus.COMPLETED);
        ReflectionTestUtils.setField(originalOrder, "id", 50L);
        OrderItem originalKimchiItem = OrderItem.create(originalOrder, currentKimchi, 1, 8000);
        OrderItem originalPorkItem = OrderItem.create(originalOrder, currentPork, 1, 10000);
        ReflectionTestUtils.setField(originalOrder, "orderItems", List.of(originalKimchiItem, originalPorkItem));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(orderRepository.findById(50L)).willReturn(Optional.of(originalOrder));
        given(menuRepository.findAllById(List.of(10L, 20L))).willReturn(List.of(currentKimchi, currentPork));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 200L);
            return order;
        });

        ReorderResponse response = orderService.reorder(50L, userId, null);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualTo(20000);
        assertThat(response.orderId()).isEqualTo(200L);
        assertThat(response.totalPrice()).isEqualTo(20000);
        assertThat(response.unavailableItems()).isEmpty();
    }

    private User createUser(Long id, String loginId) {
        User user = User.create(loginId, "password", "테스트 사용자", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Menu createMenu(Long id, String name, int price, MenuStatus status) {
        Menu menu = Menu.create(
                name,
                "KOREAN",
                price,
                name + " 설명",
                "https://example.com/" + id + ".jpg",
                15,
                status
        );
        ReflectionTestUtils.setField(menu, "id", id);
        return menu;
    }
}
