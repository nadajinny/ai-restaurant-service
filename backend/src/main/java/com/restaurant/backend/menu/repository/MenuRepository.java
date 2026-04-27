package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {
}
