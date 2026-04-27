package com.restaurant.backend.menu.dto;

public record MenuSummaryDto(
        Long id,
        String name,
        Integer price
) {
}
