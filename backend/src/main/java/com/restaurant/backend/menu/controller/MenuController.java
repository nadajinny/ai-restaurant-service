package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.menu.dto.MenuSummaryDto;
import com.restaurant.backend.menu.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/sample")
    public ApiResponse<MenuSummaryDto> getSampleMenu() {
        return ApiResponse.success(menuService.getSampleMenu());
    }
}
