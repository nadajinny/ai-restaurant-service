package com.restaurant.backend.favorite.dto;

public record FavoriteSummaryDto(
        Long id,
        Long userId,
        Long menuId
) {
}
