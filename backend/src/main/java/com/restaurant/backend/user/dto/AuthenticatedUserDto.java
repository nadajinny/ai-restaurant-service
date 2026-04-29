package com.restaurant.backend.user.dto;

public record AuthenticatedUserDto(
        Long id,
        String loginId,
        String name,
        String role
) {
}
