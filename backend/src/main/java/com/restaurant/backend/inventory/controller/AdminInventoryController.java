package com.restaurant.backend.inventory.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.inventory.dto.InventoryResponse;
import com.restaurant.backend.inventory.dto.InventoryUpdateRequest;
import com.restaurant.backend.inventory.service.InventoryService;
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
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ApiResponse<List<InventoryResponse>> getInventories() {
        return ApiResponse.success(inventoryService.getInventories());
    }

    @PutMapping("/{menuId}")
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
    public ApiResponse<InventoryResponse> markSoldOut(@PathVariable Long menuId) {
        return ApiResponse.success(
                "메뉴가 품절 처리되었습니다.",
                inventoryService.markSoldOut(menuId)
        );
    }

    @PatchMapping("/{menuId}/available")
    public ApiResponse<InventoryResponse> markAvailable(@PathVariable Long menuId) {
        return ApiResponse.success(
                "메뉴가 판매 가능 상태로 변경되었습니다.",
                inventoryService.markAvailable(menuId)
        );
    }
}
