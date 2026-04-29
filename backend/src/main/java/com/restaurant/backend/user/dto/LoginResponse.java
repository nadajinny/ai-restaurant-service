package com.restaurant.backend.user.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String expiresAt,
        AuthenticatedUserDto user
) {
}
