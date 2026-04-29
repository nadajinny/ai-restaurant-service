package com.restaurant.backend.user.dto;

public record UserSummaryDto(
        Long id,
        String loginId,
        String role
) {
}
