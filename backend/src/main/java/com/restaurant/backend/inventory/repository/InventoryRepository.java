package com.restaurant.backend.inventory.repository;

import com.restaurant.backend.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
