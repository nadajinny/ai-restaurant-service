package com.restaurant.backend.favorite.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.favorite.dto.FavoriteSummaryDto;
import com.restaurant.backend.favorite.service.FavoriteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/sample")
    public ApiResponse<FavoriteSummaryDto> getSampleFavorite() {
        return ApiResponse.success(favoriteService.getSampleFavorite());
    }
}
