package com.restaurant.backend.inventory.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.inventory.dto.InventorySummaryDto;
import com.restaurant.backend.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/sample")
    public ApiResponse<InventorySummaryDto> getSampleInventory() {
        return ApiResponse.success(inventoryService.getSampleInventory());
    }
}
