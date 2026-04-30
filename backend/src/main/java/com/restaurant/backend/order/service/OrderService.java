package com.restaurant.backend.order.service;

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
import com.restaurant.backend.order.domain.OrderStatusHistory;
import com.restaurant.backend.order.dto.OrderCreateItemRequest;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.dto.ReorderRequest;
import com.restaurant.backend.order.dto.ReorderResponse;
import com.restaurant.backend.order.dto.ReorderUnavailableItemResponse;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final CacheInvalidationService cacheInvalidationService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            MenuRepository menuRepository,
            UserRepository userRepository,
            OrderMapper orderMapper,
            InventoryService inventoryService,
            NotificationService notificationService,
            CacheInvalidationService cacheInvalidationService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Transactional
    public OrderCreateResponse createOrder(Long userId, OrderCreateRequest request) {
        Map<Long, Menu> menuMap = loadAndValidateMenus(request.items());
        User orderUser = getUserById(userId);

        // TODO: couponCode는 쿠폰 기능 구현 전까지 nullable 입력만 허용하고 실제 할인 계산에는 반영하지 않는다.
        Order order = createOrder(orderUser, request.items(), menuMap);
        return orderMapper.toOrderCreateResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderListResponse> getOrders(Long userId) {
        User user = getUserById(userId);

        return orderRepository.findAllByUser_IdOrderByCreatedAtDescIdDesc(user.getId()).stream()
                .map(orderMapper::toOrderListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(Long orderId, Long userId) {
        User user = getUserById(userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderOwnership(order, user);
        return orderMapper.toOrderDetailResponse(order);
    }

    @Transactional
    public ReorderResponse reorder(Long orderId, Long userId, ReorderRequest request) {
        User user = getUserById(userId);
        Order originalOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderOwnership(originalOrder, user);

        Set<Long> selectedMenuIds = extractSelectedMenuIds(request);
        List<OrderCreateItemRequest> reorderableItems = new ArrayList<>();
        List<ReorderUnavailableItemResponse> unavailableItems = new ArrayList<>();

        for (OrderItem originalItem : originalOrder.getOrderItems()) {
            Long menuId = originalItem.getMenu().getId();

            if (!selectedMenuIds.isEmpty() && !selectedMenuIds.contains(menuId)) {
                continue;
            }

            Menu currentMenu = originalItem.getMenu();
            if (currentMenu.getStatus() != MenuStatus.AVAILABLE) {
                unavailableItems.add(new ReorderUnavailableItemResponse(
                        currentMenu.getId(),
                        currentMenu.getName(),
                        "현재 재주문 가능한 상태가 아닙니다."
                ));
                continue;
            }

            reorderableItems.add(new OrderCreateItemRequest(menuId, originalItem.getQuantity()));
        }

        if (reorderableItems.isEmpty()) {
            throw new BusinessException(ErrorCode.REORDER_NOT_AVAILABLE);
        }

        Map<Long, Menu> menuMap = loadAndValidateMenus(reorderableItems);
        Order newOrder = createOrder(user, reorderableItems, menuMap);
        return orderMapper.toReorderResponse(newOrder, unavailableItems);
    }

    private Order createOrder(User orderUser, List<OrderCreateItemRequest> items, Map<Long, Menu> menuMap) {
        inventoryService.reserveOrderInventory(items, menuMap);
        int totalPrice = calculateTotalPrice(items, menuMap);
        Order order = orderRepository.save(Order.create(orderUser, totalPrice, OrderStatus.RECEIVED));
        orderStatusHistoryRepository.save(
                OrderStatusHistory.create(order, null, OrderStatus.RECEIVED, orderUser.getLoginId())
        );

        List<OrderItem> orderItems = items.stream()
                .map(item -> OrderItem.create(
                        order,
                        menuMap.get(item.menuId()),
                        item.quantity(),
                        menuMap.get(item.menuId()).getPrice()
                ))
                .toList();

        orderItemRepository.saveAll(orderItems);
        notificationService.createOrderReceivedNotification(order);
        cacheInvalidationService.evictUserPersonalizedRecommendations(orderUser.getId());
        cacheInvalidationService.evictAnalyticsCaches();
        return order;
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

    private Set<Long> extractSelectedMenuIds(ReorderRequest request) {
        if (request == null || request.menuIds() == null) {
            return Set.of();
        }

        return request.menuIds().stream()
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateOrderOwnership(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "다른 사용자의 주문은 조회할 수 없습니다.");
        }
    }
}
