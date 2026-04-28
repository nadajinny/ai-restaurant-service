package com.restaurant.backend.notification.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.notification.dto.NotificationResponse;
import com.restaurant.backend.notification.service.NotificationService;
import java.util.List;
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

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(@RequestParam Long userId) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success(notificationService.getNotifications(userId));
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @RequestParam Long userId,
            @PathVariable Long notificationId
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success("알림이 읽음 처리되었습니다.", notificationService.markAsRead(userId, notificationId));
    }
}
