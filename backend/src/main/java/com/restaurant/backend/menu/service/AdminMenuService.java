package com.restaurant.backend.menu.service;

import com.restaurant.backend.common.cache.CacheInvalidationService;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.inventory.domain.Inventory;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.dto.AdminMenuRequest;
import com.restaurant.backend.menu.dto.AdminMenuResponse;
import com.restaurant.backend.menu.dto.AdminMenuStatusUpdateRequest;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.repository.OrderItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminMenuService {

    private final MenuRepository menuRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuMapper menuMapper;
    private final CacheInvalidationService cacheInvalidationService;

    public AdminMenuService(
            MenuRepository menuRepository,
            InventoryRepository inventoryRepository,
            OrderItemRepository orderItemRepository,
            MenuMapper menuMapper,
            CacheInvalidationService cacheInvalidationService
    ) {
        this.menuRepository = menuRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuMapper = menuMapper;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Transactional
    public AdminMenuResponse createMenu(AdminMenuRequest request) {
        Menu menu = menuMapper.toMenu(request);
        Menu savedMenu = menuRepository.save(menu);
        inventoryRepository.findByMenu_Id(savedMenu.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.create(savedMenu, 0)));
        cacheInvalidationService.evictMenuCaches(savedMenu.getId());
        return menuMapper.toAdminMenuResponse(savedMenu);
    }

    @Transactional(readOnly = true)
    public List<AdminMenuResponse> getMenus() {
        return menuRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(menuMapper::toAdminMenuResponse)
                .toList();
    }

    @Transactional
    public AdminMenuResponse updateMenu(Long menuId, AdminMenuRequest request) {
        Menu menu = getMenuById(menuId);
        menu.update(
                request.name(),
                request.category(),
                request.price(),
                request.description(),
                request.imageUrl(),
                request.cookingTime(),
                request.status()
        );

        cacheInvalidationService.evictMenuCaches(menu.getId());
        return menuMapper.toAdminMenuResponse(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = getMenuById(menuId);

        if (orderItemRepository.existsByMenu_Id(menuId)) {
            menu.hide();
            cacheInvalidationService.evictMenuCaches(menu.getId());
            return;
        }

        inventoryRepository.findByMenu_Id(menuId)
                .ifPresent(inventoryRepository::delete);
        menuRepository.delete(menu);
        cacheInvalidationService.evictMenuCaches(menuId);
    }

    @Transactional
    public AdminMenuResponse updateStatus(Long menuId, AdminMenuStatusUpdateRequest request) {
        Menu menu = getMenuById(menuId);
        menu.changeStatus(request.status());
        cacheInvalidationService.evictMenuCaches(menu.getId());
        return menuMapper.toAdminMenuResponse(menu);
    }

    private Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }
}
