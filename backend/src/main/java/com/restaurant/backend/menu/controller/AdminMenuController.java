package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.menu.dto.AdminMenuRequest;
import com.restaurant.backend.menu.dto.AdminMenuResponse;
import com.restaurant.backend.menu.dto.AdminMenuStatusUpdateRequest;
import com.restaurant.backend.menu.service.AdminMenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/menus")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    @PostMapping
    public ApiResponse<AdminMenuResponse> createMenu(@Valid @RequestBody AdminMenuRequest request) {
        return ApiResponse.success("메뉴가 등록되었습니다.", adminMenuService.createMenu(request));
    }

    @PutMapping("/{menuId}")
    public ApiResponse<AdminMenuResponse> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody AdminMenuRequest request
    ) {
        return ApiResponse.success("메뉴가 수정되었습니다.", adminMenuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteMenu(@PathVariable Long menuId) {
        adminMenuService.deleteMenu(menuId);
        return ApiResponse.success("메뉴가 삭제되었습니다.", null);
    }

    @PatchMapping("/{menuId}/status")
    public ApiResponse<AdminMenuResponse> updateStatus(
            @PathVariable Long menuId,
            @Valid @RequestBody AdminMenuStatusUpdateRequest request
    ) {
        return ApiResponse.success("메뉴 상태가 변경되었습니다.", adminMenuService.updateStatus(menuId, request));
    }
}
