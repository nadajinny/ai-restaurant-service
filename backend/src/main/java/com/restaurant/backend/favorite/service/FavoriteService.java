package com.restaurant.backend.favorite.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.favorite.domain.Favorite;
import com.restaurant.backend.favorite.dto.FavoriteCreateRequest;
import com.restaurant.backend.favorite.dto.FavoriteResponse;
import com.restaurant.backend.favorite.repository.FavoriteRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            MenuRepository menuRepository,
            UserRepository userRepository,
            FavoriteMapper favoriteMapper
    ) {
        this.favoriteRepository = favoriteRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional
    public FavoriteResponse createFavorite(Long userId, FavoriteCreateRequest request) {
        User user = getUserById(userId);
        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByUser_IdAndMenu_Id(userId, request.menuId())
                .orElseGet(() -> favoriteRepository.save(Favorite.create(user, menu)));

        return favoriteMapper.toFavoriteResponse(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavorites(Long userId) {
        getUserById(userId);

        return favoriteRepository.findAllByUser_IdOrderByCreatedAtDescIdDesc(userId).stream()
                .map(favoriteMapper::toFavoriteResponse)
                .toList();
    }

    @Transactional
    public void deleteFavorite(Long userId, Long menuId) {
        getUserById(userId);

        Favorite favorite = favoriteRepository.findByUser_IdAndMenu_Id(userId, menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
