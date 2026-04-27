package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
