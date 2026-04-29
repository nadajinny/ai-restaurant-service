package com.restaurant.backend.notification.dto;

import com.restaurant.backend.notification.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
}
