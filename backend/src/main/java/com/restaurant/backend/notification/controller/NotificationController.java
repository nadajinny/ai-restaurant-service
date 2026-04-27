package com.restaurant.backend.notification.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.notification.dto.NotificationSummaryDto;
import com.restaurant.backend.notification.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/sample")
    public ApiResponse<NotificationSummaryDto> getSampleNotification() {
        return ApiResponse.success(notificationService.getSampleNotification());
    }
}
