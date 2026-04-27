package com.restaurant.backend.favorite.dto;

import com.restaurant.backend.menu.domain.MenuStatus;

public record FavoriteResponse(
        Long menuId,
        String name,
        Integer price,
        String imageUrl,
        String category,
        MenuStatus status
) {
}
