package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.menu.dto.MenuDetailResponse;
import com.restaurant.backend.menu.dto.MenuListResponse;
import com.restaurant.backend.menu.dto.MenuSearchRequest;
import com.restaurant.backend.menu.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ApiResponse<List<MenuListResponse>> getMenus(@Valid @ModelAttribute MenuSearchRequest request) {
        return ApiResponse.success(menuService.getMenus(request));
    }

    @GetMapping("/{menuId}")
    public ApiResponse<MenuDetailResponse> getMenu(@PathVariable Long menuId) {
        return ApiResponse.success(menuService.getMenu(menuId));
    }
}
