package com.restaurant.backend.favorite.service;

import com.restaurant.backend.favorite.domain.Favorite;
import com.restaurant.backend.favorite.dto.FavoriteResponse;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteResponse toFavoriteResponse(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getMenu().getId(),
                favorite.getMenu().getName(),
                favorite.getMenu().getPrice(),
                favorite.getMenu().getImageUrl(),
                favorite.getMenu().getCategory(),
                favorite.getMenu().getStatus()
        );
    }
}
