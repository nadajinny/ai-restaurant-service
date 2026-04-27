package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    Optional<Menu> findByIdAndStatusNot(Long id, MenuStatus status);
}
