package com.restaurant.backend.favorite.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.favorite.dto.FavoriteCreateRequest;
import com.restaurant.backend.favorite.dto.FavoriteResponse;
import com.restaurant.backend.favorite.service.FavoriteService;
import com.restaurant.backend.user.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final CurrentUserService currentUserService;

    public FavoriteController(FavoriteService favoriteService, CurrentUserService currentUserService) {
        this.favoriteService = favoriteService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<FavoriteResponse> createFavorite(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @Valid @RequestBody FavoriteCreateRequest request
    ) {
        return ApiResponse.success(
                "즐겨찾기가 추가되었습니다.",
                favoriteService.createFavorite(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        return ApiResponse.success(favoriteService.getFavorites(currentUserService.getCurrentUserId(authentication)));
    }

    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteFavorite(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @PathVariable Long menuId
    ) {
        favoriteService.deleteFavorite(currentUserService.getCurrentUserId(authentication), menuId);
        return ApiResponse.success("즐겨찾기가 해제되었습니다.", null);
    }
}
