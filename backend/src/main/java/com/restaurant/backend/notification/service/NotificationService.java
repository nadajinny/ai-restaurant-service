package com.restaurant.backend.notification.service;

import com.restaurant.backend.notification.dto.NotificationSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public NotificationSummaryDto getSampleNotification() {
        return new NotificationSummaryDto(1L, "ORDER_READY", "Your order is ready.");
    }
}
