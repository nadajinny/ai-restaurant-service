package com.restaurant.backend.review.dto;

public record ReviewSummaryDto(
        Long id,
        Long menuId,
        Integer rating
) {
}
