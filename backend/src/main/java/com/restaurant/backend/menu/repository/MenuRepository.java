package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByStatusNotOrderByIdAsc(MenuStatus status);

    List<Menu> findAllByCategoryAndStatusNotOrderByIdAsc(String category, MenuStatus status);

    Optional<Menu> findByIdAndStatusNot(Long id, MenuStatus status);
}
