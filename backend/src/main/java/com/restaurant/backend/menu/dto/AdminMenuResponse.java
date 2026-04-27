package com.restaurant.backend.menu.dto;

import com.restaurant.backend.menu.domain.MenuStatus;

public record AdminMenuResponse(
        Long menuId,
        String name,
        Integer price,
        String category,
        String description,
        String imageUrl,
        Integer cookingTime,
        MenuStatus status
) {
}
