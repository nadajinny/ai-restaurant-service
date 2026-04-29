package com.restaurant.backend.menu.dto;

import com.restaurant.backend.menu.domain.MenuStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdminMenuRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,
        @NotBlank(message = "카테고리는 필수입니다.")
        String category,
        @NotNull(message = "가격은 필수입니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        Integer price,
        @NotBlank(message = "설명은 필수입니다.")
        String description,
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,
        @NotNull(message = "예상 조리 시간은 필수입니다.")
        @Positive(message = "예상 조리 시간은 0보다 커야 합니다.")
        Integer cookingTime,
        @NotNull(message = "판매 상태는 필수입니다.")
        MenuStatus status
) {
}
