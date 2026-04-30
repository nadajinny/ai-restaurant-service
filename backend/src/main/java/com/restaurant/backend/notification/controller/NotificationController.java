package com.restaurant.backend.notification.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.notification.dto.NotificationResponse;
import com.restaurant.backend.notification.service.NotificationService;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Tag(name = "알림", description = "사용자 알림 API")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponse>> getNotifications(Authentication authentication) {
        return ApiResponse.success(notificationService.getNotifications(currentUserService.getCurrentUserId(authentication)));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "로그인한 사용자의 알림을 읽음 상태로 변경합니다.")
    public ApiResponse<NotificationResponse> markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        return ApiResponse.success(
                "알림이 읽음 처리되었습니다.",
                notificationService.markAsRead(currentUserService.getCurrentUserId(authentication), notificationId)
        );
    }
}
