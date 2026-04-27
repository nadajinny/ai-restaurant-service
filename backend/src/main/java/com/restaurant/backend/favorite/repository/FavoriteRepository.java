package com.restaurant.backend.favorite.repository;

import com.restaurant.backend.favorite.domain.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUser_IdAndMenu_Id(Long userId, Long menuId);

    List<Favorite> findAllByUser_IdOrderByCreatedAtDescIdDesc(Long userId);
}
