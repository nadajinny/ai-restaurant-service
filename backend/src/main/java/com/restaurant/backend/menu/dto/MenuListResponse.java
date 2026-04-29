package com.restaurant.backend.menu.dto;

import com.restaurant.backend.menu.domain.MenuStatus;

public record MenuListResponse(
        Long menuId,
        String name,
        Integer price,
        String category,
        String imageUrl,
        Integer cookingTime,
        MenuStatus status,
        boolean orderable
) {
}
