package com.restaurant.backend.favorite.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.favorite.dto.FavoriteCreateRequest;
import com.restaurant.backend.favorite.dto.FavoriteResponse;
import com.restaurant.backend.favorite.service.FavoriteService;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorites")
@Tag(name = "즐겨찾기", description = "메뉴 즐겨찾기 API")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final CurrentUserService currentUserService;

    public FavoriteController(FavoriteService favoriteService, CurrentUserService currentUserService) {
        this.favoriteService = favoriteService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "즐겨찾기 추가", description = "로그인한 사용자의 즐겨찾기에 메뉴를 추가합니다.")
    public ApiResponse<FavoriteResponse> createFavorite(
            Authentication authentication,
            @Valid @RequestBody FavoriteCreateRequest request
    ) {
        return ApiResponse.success(
                "즐겨찾기가 추가되었습니다.",
                favoriteService.createFavorite(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @GetMapping
    @Operation(summary = "즐겨찾기 목록 조회", description = "로그인한 사용자의 즐겨찾기 메뉴 목록을 조회합니다.")
    public ApiResponse<List<FavoriteResponse>> getFavorites(Authentication authentication) {
        return ApiResponse.success(favoriteService.getFavorites(currentUserService.getCurrentUserId(authentication)));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "즐겨찾기 해제", description = "로그인한 사용자의 즐겨찾기에서 메뉴를 제거합니다.")
    public ApiResponse<Void> deleteFavorite(
            Authentication authentication,
            @PathVariable Long menuId
    ) {
        favoriteService.deleteFavorite(currentUserService.getCurrentUserId(authentication), menuId);
        return ApiResponse.success("즐겨찾기가 해제되었습니다.", null);
    }
}
