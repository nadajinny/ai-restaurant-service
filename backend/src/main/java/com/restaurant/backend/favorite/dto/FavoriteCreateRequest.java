package com.restaurant.backend.favorite.dto;

import jakarta.validation.constraints.NotNull;

public record FavoriteCreateRequest(
        @NotNull(message = "menuId는 필수입니다.")
        Long menuId
) {
}
