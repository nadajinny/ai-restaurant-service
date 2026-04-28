package com.restaurant.backend.notification.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.notification.dto.NotificationResponse;
import com.restaurant.backend.notification.service.NotificationService;
import com.restaurant.backend.user.service.CurrentUserService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(
            NotificationService notificationService,
            CurrentUserService currentUserService
    ) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        return ApiResponse.success(notificationService.getNotifications(currentUserService.getCurrentUserId(authentication)));
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        return ApiResponse.success(
                "알림이 읽음 처리되었습니다.",
                notificationService.markAsRead(currentUserService.getCurrentUserId(authentication), notificationId)
        );
    }
}
