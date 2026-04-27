package com.restaurant.backend.notification.service;

import com.restaurant.backend.notification.domain.Notification;
import com.restaurant.backend.notification.dto.NotificationSummaryDto;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationSummaryDto getSampleNotification() {
        return new NotificationSummaryDto(1L, "ORDER_READY", "Your order is ready.");
    }

    @Transactional
    public void createOrderStatusChangedNotification(Order order, OrderStatus fromStatus, OrderStatus toStatus) {
        // TODO: 추후 Notification Service 분리 또는 이벤트 기반 처리로 대체할 수 있다.
        String content = "주문 상태가 " + fromStatus + "에서 " + toStatus + "로 변경되었습니다.";
        Notification notification = Notification.create(order.getUser(), "ORDER_STATUS_CHANGED", content);
        notificationRepository.save(notification);
    }
}
