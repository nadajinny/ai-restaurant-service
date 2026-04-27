package com.restaurant.backend.favorite.service;

import com.restaurant.backend.favorite.dto.FavoriteSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    public FavoriteSummaryDto getSampleFavorite() {
        return new FavoriteSummaryDto(1L, 1L, 1L);
    }
}
