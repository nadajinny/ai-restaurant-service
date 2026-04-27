package com.restaurant.backend.inventory.service;

import com.restaurant.backend.inventory.dto.InventorySummaryDto;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public InventorySummaryDto getSampleInventory() {
        return new InventorySummaryDto(1L, 1L, 10);
    }
}
