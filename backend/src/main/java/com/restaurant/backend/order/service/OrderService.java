package com.restaurant.backend.order.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.dto.OrderCreateItemRequest;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final String TEMPORARY_ORDER_USER_LOGIN_ID = "temporary-order-user";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            MenuRepository menuRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        Map<Long, Menu> menuMap = loadAndValidateMenus(request.items());
        int totalPrice = calculateTotalPrice(request.items(), menuMap);

        // TODO: 인증 기능 구현 후 실제 로그인 사용자로 대체한다.
        User orderUser = resolveOrderUser();

        // TODO: couponCode는 쿠폰 기능 구현 전까지 nullable 입력만 허용하고 실제 할인 계산에는 반영하지 않는다.
        Order order = orderRepository.save(Order.create(orderUser, totalPrice, OrderStatus.RECEIVED));

        List<OrderItem> orderItems = request.items().stream()
                .map(item -> OrderItem.create(
                        order,
                        menuMap.get(item.menuId()),
                        item.quantity(),
                        menuMap.get(item.menuId()).getPrice()
                ))
                .toList();

        orderItemRepository.saveAll(orderItems);

        return new OrderCreateResponse(order.getId(), order.getStatus(), order.getTotalPrice());
    }

    private Map<Long, Menu> loadAndValidateMenus(List<OrderCreateItemRequest> items) {
        List<Long> menuIds = items.stream()
                .map(OrderCreateItemRequest::menuId)
                .distinct()
                .toList();

        Map<Long, Menu> menuMap = menuRepository.findAllById(menuIds).stream()
                .collect(java.util.stream.Collectors.toMap(Menu::getId, Function.identity()));

        for (OrderCreateItemRequest item : items) {
            Menu menu = menuMap.get(item.menuId());

            if (menu == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            }

            if (menu.getStatus() != MenuStatus.AVAILABLE) {
                throw new BusinessException(
                        ErrorCode.MENU_NOT_ORDERABLE,
                        "주문 가능한 메뉴가 아닙니다. menuId=" + item.menuId()
                );
            }
        }

        return menuMap;
    }

    private int calculateTotalPrice(List<OrderCreateItemRequest> items, Map<Long, Menu> menuMap) {
        int totalPrice = 0;

        for (OrderCreateItemRequest item : items) {
            Menu menu = menuMap.get(item.menuId());
            totalPrice += menu.getPrice() * item.quantity();
        }

        return totalPrice;
    }

    private User resolveOrderUser() {
        return userRepository.findByLoginId(TEMPORARY_ORDER_USER_LOGIN_ID)
                .orElseGet(() -> userRepository.save(User.create(
                        TEMPORARY_ORDER_USER_LOGIN_ID,
                        "temporary-password",
                        "임시 주문 사용자",
                        UserRole.USER
                )));
    }
}
