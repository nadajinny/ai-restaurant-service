package com.restaurant.backend.inventory.repository;

import com.restaurant.backend.inventory.domain.Inventory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMenu_Id(Long menuId);

    List<Inventory> findAllByMenu_IdIn(Collection<Long> menuIds);
}
