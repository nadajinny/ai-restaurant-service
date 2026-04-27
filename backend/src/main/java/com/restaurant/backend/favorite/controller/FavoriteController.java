package com.restaurant.backend.favorite.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.favorite.dto.FavoriteCreateRequest;
import com.restaurant.backend.favorite.dto.FavoriteResponse;
import com.restaurant.backend.favorite.service.FavoriteService;
import jakarta.validation.Valid;
import java.util.List;
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

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ApiResponse<FavoriteResponse> createFavorite(
            @RequestParam Long userId,
            @Valid @RequestBody FavoriteCreateRequest request
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success("즐겨찾기가 추가되었습니다.", favoriteService.createFavorite(userId, request));
    }

    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(@RequestParam Long userId) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success(favoriteService.getFavorites(userId));
    }

    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteFavorite(@RequestParam Long userId, @PathVariable Long menuId) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        favoriteService.deleteFavorite(userId, menuId);
        return ApiResponse.success("즐겨찾기가 해제되었습니다.", null);
    }
}
