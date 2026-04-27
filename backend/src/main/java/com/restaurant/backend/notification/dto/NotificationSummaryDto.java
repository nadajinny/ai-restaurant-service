package com.restaurant.backend.notification.dto;

public record NotificationSummaryDto(
        Long id,
        String type,
        String content
) {
}
