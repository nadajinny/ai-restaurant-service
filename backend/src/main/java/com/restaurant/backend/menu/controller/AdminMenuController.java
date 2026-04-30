package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.menu.dto.AdminMenuRequest;
import com.restaurant.backend.menu.dto.AdminMenuResponse;
import com.restaurant.backend.menu.dto.AdminMenuStatusUpdateRequest;
import com.restaurant.backend.menu.service.AdminMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/menus")
@Tag(name = "관리자 메뉴", description = "관리자 메뉴 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    @GetMapping
    @Operation(summary = "전체 메뉴 조회", description = "관리자 권한으로 전체 메뉴 목록을 조회합니다.")
    public ApiResponse<List<AdminMenuResponse>> getMenus() {
        return ApiResponse.success(adminMenuService.getMenus());
    }

    @PostMapping
    @Operation(summary = "메뉴 등록", description = "관리자 권한으로 신규 메뉴를 등록합니다.")
    public ApiResponse<AdminMenuResponse> createMenu(@Valid @RequestBody AdminMenuRequest request) {
        return ApiResponse.success("메뉴가 등록되었습니다.", adminMenuService.createMenu(request));
    }

    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "관리자 권한으로 기존 메뉴 정보를 수정합니다.")
    public ApiResponse<AdminMenuResponse> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody AdminMenuRequest request
    ) {
        return ApiResponse.success("메뉴가 수정되었습니다.", adminMenuService.updateMenu(menuId, request));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제", description = "관리자 권한으로 메뉴를 삭제합니다.")
    public ApiResponse<Void> deleteMenu(@PathVariable Long menuId) {
        adminMenuService.deleteMenu(menuId);
        return ApiResponse.success("메뉴가 삭제되었습니다.", null);
    }

    @PatchMapping("/{menuId}/status")
    @Operation(summary = "메뉴 상태 변경", description = "관리자 권한으로 메뉴의 판매 상태를 변경합니다.")
    public ApiResponse<AdminMenuResponse> updateStatus(
            @PathVariable Long menuId,
            @Valid @RequestBody AdminMenuStatusUpdateRequest request
    ) {
        return ApiResponse.success("메뉴 상태가 변경되었습니다.", adminMenuService.updateStatus(menuId, request));
    }
}
