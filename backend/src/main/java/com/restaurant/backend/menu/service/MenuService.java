package com.restaurant.backend.menu.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.dto.MenuDetailResponse;
import com.restaurant.backend.menu.dto.MenuListResponse;
import com.restaurant.backend.menu.repository.MenuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuListResponse> getMenus(String category) {
        List<Menu> menus = StringUtils.hasText(category)
                ? menuRepository.findAllByCategoryAndStatusNotOrderByIdAsc(category, MenuStatus.HIDDEN)
                : menuRepository.findAllByStatusNotOrderByIdAsc(MenuStatus.HIDDEN);

        return menus.stream()
                .map(this::toMenuListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuDetailResponse getMenu(Long menuId) {
        Menu menu = menuRepository.findByIdAndStatusNot(menuId, MenuStatus.HIDDEN)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        return toMenuDetailResponse(menu);
    }

    private MenuListResponse toMenuListResponse(Menu menu) {
        return new MenuListResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getImageUrl(),
                menu.getCookingTime(),
                menu.getStatus(),
                isOrderable(menu)
        );
    }

    private MenuDetailResponse toMenuDetailResponse(Menu menu) {
        return new MenuDetailResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getDescription(),
                menu.getImageUrl(),
                menu.getCookingTime(),
                menu.getStatus(),
                isOrderable(menu)
        );
    }

    private boolean isOrderable(Menu menu) {
        return menu.getStatus() == MenuStatus.AVAILABLE;
    }
}
