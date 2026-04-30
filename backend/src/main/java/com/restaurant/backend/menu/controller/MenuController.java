package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.menu.dto.MenuDetailResponse;
import com.restaurant.backend.menu.dto.MenuListResponse;
import com.restaurant.backend.menu.dto.MenuSearchRequest;
import com.restaurant.backend.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "메뉴", description = "고객용 메뉴 조회 API")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(summary = "메뉴 목록 조회", description = "카테고리, 상태, 정렬 조건으로 메뉴 목록을 조회합니다.")
    public ApiResponse<List<MenuListResponse>> getMenus(@Valid @ModelAttribute MenuSearchRequest request) {
        return ApiResponse.success(menuService.getMenus(request));
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "메뉴 ID로 상세 정보를 조회합니다.")
    public ApiResponse<MenuDetailResponse> getMenu(@PathVariable Long menuId) {
        return ApiResponse.success(menuService.getMenu(menuId));
    }
}
