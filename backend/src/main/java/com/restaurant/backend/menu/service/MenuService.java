package com.restaurant.backend.menu.service;

import com.restaurant.backend.common.cache.CacheKeyFactory;
import com.restaurant.backend.common.cache.CacheNames;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.dto.MenuDetailResponse;
import com.restaurant.backend.menu.dto.MenuListResponse;
import com.restaurant.backend.menu.dto.MenuSearchRequest;
import com.restaurant.backend.menu.dto.MenuSortType;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.menu.repository.MenuSpecification;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuService(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.MENUS, key = "T(com.restaurant.backend.common.cache.CacheKeyFactory).menuSearch(#request)")
    public List<MenuListResponse> getMenus(MenuSearchRequest request) {
        validateSearchRequest(request);

        List<Menu> menus = menuRepository.findAll(
                MenuSpecification.bySearchRequest(request),
                buildSort(request.getSort())
        );

        return menus.stream()
                .map(menuMapper::toMenuListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.MENU_DETAILS, key = "#menuId")
    public MenuDetailResponse getMenu(Long menuId) {
        Menu menu = menuRepository.findByIdAndStatusNot(menuId, MenuStatus.HIDDEN)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        return menuMapper.toMenuDetailResponse(menu);
    }

    private void validateSearchRequest(MenuSearchRequest request) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice() > request.getMaxPrice()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "minPrice는 maxPrice보다 클 수 없습니다.");
        }

        if (request.getStatus() == MenuStatus.HIDDEN) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "고객용 조회에서는 HIDDEN 상태를 조회할 수 없습니다.");
        }
    }

    private Sort buildSort(MenuSortType sortType) {
        MenuSortType effectiveSortType = sortType == null ? MenuSortType.LATEST : sortType;

        return switch (effectiveSortType) {
            case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price").and(Sort.by(Sort.Direction.DESC, "id"));
            case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price").and(Sort.by(Sort.Direction.DESC, "id"));
            case POPULAR -> {
                // TODO: popularity metric is not modeled yet. Fallback to latest ordering for now.
                yield latestSort();
            }
            case RATING -> {
                // TODO: average rating projection is not modeled yet. Fallback to latest ordering for now.
                yield latestSort();
            }
            case LATEST -> latestSort();
        };
    }

    private Sort latestSort() {
        return Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
