package com.restaurant.backend.menu.service;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.dto.AdminMenuRequest;
import com.restaurant.backend.menu.dto.AdminMenuResponse;
import com.restaurant.backend.menu.dto.MenuDetailResponse;
import com.restaurant.backend.menu.dto.MenuListResponse;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public MenuListResponse toMenuListResponse(Menu menu) {
        return new MenuListResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getImageUrl(),
                menu.getCookingTime(),
                menu.getStatus(),
                isOrderable(menu)
        );
    }

    public MenuDetailResponse toMenuDetailResponse(Menu menu) {
        return new MenuDetailResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getDescription(),
                menu.getImageUrl(),
                menu.getCookingTime(),
                menu.getStatus(),
                isOrderable(menu)
        );
    }

    public AdminMenuResponse toAdminMenuResponse(Menu menu) {
        return new AdminMenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getDescription(),
                menu.getImageUrl(),
                menu.getCookingTime(),
                menu.getStatus()
        );
    }

    public Menu toMenu(AdminMenuRequest request) {
        return Menu.create(
                request.name(),
                request.category(),
                request.price(),
                request.description(),
                request.imageUrl(),
                request.cookingTime(),
                request.status()
        );
    }

    private boolean isOrderable(Menu menu) {
        return menu.getStatus() == MenuStatus.AVAILABLE;
    }
}
