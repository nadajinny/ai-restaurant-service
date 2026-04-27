package com.restaurant.backend.menu.dto;

import com.restaurant.backend.menu.domain.MenuStatus;
import jakarta.validation.constraints.NotNull;

public record AdminMenuStatusUpdateRequest(
        @NotNull(message = "판매 상태는 필수입니다.")
        MenuStatus status
) {
}
