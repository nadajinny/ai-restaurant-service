package com.restaurant.backend.favorite.repository;

import com.restaurant.backend.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
