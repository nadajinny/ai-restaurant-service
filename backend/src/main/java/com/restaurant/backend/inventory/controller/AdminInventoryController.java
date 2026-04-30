package com.restaurant.backend.inventory.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.inventory.dto.InventoryResponse;
import com.restaurant.backend.inventory.dto.InventoryUpdateRequest;
import com.restaurant.backend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/inventories")
@Tag(name = "관리자 재고", description = "관리자 재고 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "재고 목록 조회", description = "관리자 권한으로 메뉴별 재고 현황을 조회합니다.")
    public ApiResponse<List<InventoryResponse>> getInventories() {
        return ApiResponse.success(inventoryService.getInventories());
    }

    @PutMapping("/{menuId}")
    @Operation(summary = "재고 수정", description = "관리자 권한으로 특정 메뉴의 재고를 수정합니다.")
    public ApiResponse<InventoryResponse> updateInventory(
            @PathVariable Long menuId,
            @Valid @RequestBody InventoryUpdateRequest request
    ) {
        return ApiResponse.success(
                "재고가 수정되었습니다.",
                inventoryService.updateInventory(menuId, request)
        );
    }

    @PatchMapping("/{menuId}/sold-out")
    @Operation(summary = "품절 처리", description = "관리자 권한으로 메뉴를 품절 상태로 변경합니다.")
    public ApiResponse<InventoryResponse> markSoldOut(@PathVariable Long menuId) {
        return ApiResponse.success(
                "메뉴가 품절 처리되었습니다.",
                inventoryService.markSoldOut(menuId)
        );
    }

    @PatchMapping("/{menuId}/available")
    @Operation(summary = "판매 가능 처리", description = "관리자 권한으로 메뉴를 다시 판매 가능 상태로 변경합니다.")
    public ApiResponse<InventoryResponse> markAvailable(@PathVariable Long menuId) {
        return ApiResponse.success(
                "메뉴가 판매 가능 상태로 변경되었습니다.",
                inventoryService.markAvailable(menuId)
        );
    }
}
