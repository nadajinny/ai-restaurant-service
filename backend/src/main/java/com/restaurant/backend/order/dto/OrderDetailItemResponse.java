package com.restaurant.backend.order.dto;

public record OrderDetailItemResponse(
        Long menuId,
        String menuName,
        Integer quantity,
        Integer itemPrice
) {
}
