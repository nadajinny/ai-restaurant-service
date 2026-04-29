package com.restaurant.backend.notification.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.notification.domain.Notification;
import com.restaurant.backend.notification.domain.NotificationType;
import com.restaurant.backend.notification.dto.NotificationResponse;
import com.restaurant.backend.notification.repository.NotificationRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPublisher notificationPublisher;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationPublisher notificationPublisher
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        getUserById(userId);

        return notificationRepository.findAllByUser_IdOrderByCreatedAtDescIdDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        User user = getUserById(userId);
        Notification notification = getNotificationById(notificationId);

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "자신의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
        return toResponse(notification);
    }

    @Transactional
    public void createOrderReceivedNotification(Order order) {
        saveAndPublish(order.getUser(), NotificationType.ORDER_RECEIVED, "주문이 정상적으로 접수되었습니다.");
    }

    @Transactional
    public void createOrderStatusNotification(Order order, OrderStatus status) {
        NotificationType type = mapOrderStatusToNotificationType(status);
        String content = buildOrderStatusContent(status);
        saveAndPublish(order.getUser(), type, content);
    }

    @Transactional
    public void createPaymentFailedNotification(Order order) {
        saveAndPublish(order.getUser(), NotificationType.PAYMENT_FAILED, "결제에 실패하여 주문이 취소되었습니다.");
    }

    private NotificationType mapOrderStatusToNotificationType(OrderStatus status) {
        return switch (status) {
            case RECEIVED -> NotificationType.ORDER_RECEIVED;
            case COOKING -> NotificationType.ORDER_COOKING;
            case READY -> NotificationType.ORDER_READY;
            case COMPLETED -> NotificationType.ORDER_COMPLETED;
            case CANCELED -> NotificationType.ORDER_CANCELED;
        };
    }

    private String buildOrderStatusContent(OrderStatus status) {
        return switch (status) {
            case RECEIVED -> "주문이 정상적으로 접수되었습니다.";
            case COOKING -> "주문한 음식의 조리가 시작되었습니다.";
            case READY -> "주문한 음식의 준비가 완료되었습니다.";
            case COMPLETED -> "주문이 완료되었습니다.";
            case CANCELED -> "주문이 취소되었습니다.";
        };
    }

    private void saveAndPublish(User user, NotificationType type, String content) {
        Notification notification = notificationRepository.save(Notification.create(user, type, content));
        notificationPublisher.publish(notification);
    }

    private Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
