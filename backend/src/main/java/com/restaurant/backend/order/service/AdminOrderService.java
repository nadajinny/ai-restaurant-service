package com.restaurant.backend.order.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.inventory.service.InventoryService;
import com.restaurant.backend.notification.service.NotificationService;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.domain.OrderStatusHistory;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateRequest;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateResponse;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.order.repository.OrderStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminOrderService {

    private static final String SYSTEM_ADMIN = "system-admin";

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final NotificationService notificationService;
    private final InventoryService inventoryService;

    public AdminOrderService(
            OrderRepository orderRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            NotificationService notificationService,
            InventoryService inventoryService
    ) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.notificationService = notificationService;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public AdminOrderStatusUpdateResponse updateOrderStatus(Long orderId, AdminOrderStatusUpdateRequest request) {
        // TODO: 관리자 권한 검증은 인증 기능 구현 후 적용한다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus nextStatus = request.status();

        if (!order.canTransitionTo(nextStatus)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "허용되지 않는 주문 상태 변경입니다. " + currentStatus + " -> " + nextStatus
            );
        }

        order.changeStatus(nextStatus);
        if (nextStatus == OrderStatus.CANCELED) {
            inventoryService.restoreInventoryForCanceledOrder(order);
        }
        orderStatusHistoryRepository.save(
                OrderStatusHistory.create(order, currentStatus, nextStatus, SYSTEM_ADMIN)
        );

        notificationService.createOrderStatusNotification(order, nextStatus);

        return new AdminOrderStatusUpdateResponse(order.getId(), order.getStatus());
    }
}
