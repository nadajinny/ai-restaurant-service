package com.restaurant.backend.inventory.dto;

import com.restaurant.backend.menu.domain.MenuStatus;

public record InventoryResponse(
        Long menuId,
        String menuName,
        String category,
        Integer quantity,
        MenuStatus status
) {
}
