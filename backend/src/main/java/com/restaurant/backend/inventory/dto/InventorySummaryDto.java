package com.restaurant.backend.inventory.dto;

public record InventorySummaryDto(
        Long id,
        Long menuId,
        Integer quantity
) {
}
