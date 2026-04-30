package com.restaurant.backend.inventory.service;

import com.restaurant.backend.common.cache.CacheInvalidationService;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.inventory.domain.Inventory;
import com.restaurant.backend.inventory.dto.InventoryResponse;
import com.restaurant.backend.inventory.dto.InventoryUpdateRequest;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.dto.OrderCreateItemRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MenuRepository menuRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public InventoryService(
            InventoryRepository inventoryRepository,
            MenuRepository menuRepository,
            CacheInvalidationService cacheInvalidationService
    ) {
        this.inventoryRepository = inventoryRepository;
        this.menuRepository = menuRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventories() {
        Map<Long, Inventory> inventoryMap = inventoryRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                        inventory -> inventory.getMenu().getId(),
                        Function.identity()
                ));

        return menuRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Menu::getId))
                .map(menu -> toInventoryResponse(menu, inventoryMap.get(menu.getId())))
                .toList();
    }

    @Transactional
    public InventoryResponse updateInventory(Long menuId, InventoryUpdateRequest request) {
        Menu menu = getMenuById(menuId);
        Inventory inventory = inventoryRepository.findByMenu_Id(menuId)
                .orElseGet(() -> inventoryRepository.save(Inventory.create(menu, 0)));

        inventory.updateQuantity(request.quantity());
        syncMenuStatusWithQuantity(menu, inventory.getQuantity());
        cacheInvalidationService.evictMenuAndAnalyticsCaches(menuId);

        return toInventoryResponse(menu, inventory);
    }

    @Transactional
    public InventoryResponse markSoldOut(Long menuId) {
        Menu menu = getMenuById(menuId);
        menu.changeStatus(MenuStatus.SOLD_OUT);
        Inventory inventory = inventoryRepository.findByMenu_Id(menuId).orElse(null);
        cacheInvalidationService.evictMenuAndAnalyticsCaches(menuId);
        return toInventoryResponse(menu, inventory);
    }

    @Transactional
    public InventoryResponse markAvailable(Long menuId) {
        Menu menu = getMenuById(menuId);
        Inventory inventory = inventoryRepository.findByMenu_Id(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));

        if (inventory.getQuantity() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "재고가 0인 메뉴는 판매 가능 상태로 변경할 수 없습니다.");
        }

        menu.changeStatus(MenuStatus.AVAILABLE);
        cacheInvalidationService.evictMenuAndAnalyticsCaches(menuId);
        return toInventoryResponse(menu, inventory);
    }

    @Transactional
    public void reserveOrderInventory(List<OrderCreateItemRequest> items, Map<Long, Menu> menuMap) {
        Map<Long, Integer> requestedQuantities = aggregateRequestedQuantities(items);
        Map<Long, Inventory> inventoryMap = loadInventories(requestedQuantities.keySet());

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Long menuId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            Inventory inventory = inventoryMap.get(menuId);

            if (inventory == null) {
                throw new BusinessException(ErrorCode.INVENTORY_NOT_FOUND, "재고 정보를 찾을 수 없습니다. menuId=" + menuId);
            }

            if (inventory.getQuantity() < requestedQuantity) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다. menuId=" + menuId);
            }
        }

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Long menuId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            Inventory inventory = inventoryMap.get(menuId);
            Menu menu = menuMap.get(menuId);

            inventory.decrease(requestedQuantity);
            if (inventory.getQuantity() == 0) {
                menu.changeStatus(MenuStatus.SOLD_OUT);
            }
        }

        requestedQuantities.keySet().forEach(cacheInvalidationService::evictMenuAndAnalyticsCaches);
    }

    @Transactional
    public void restoreInventoryForCanceledOrder(Order order) {
        // TODO: 주문 취소 정책이 확장되면 이벤트 기반 보상 처리로 분리할 수 있다.
        for (OrderItem orderItem : order.getOrderItems()) {
            Menu menu = orderItem.getMenu();
            Inventory inventory = inventoryRepository.findByMenu_Id(menu.getId())
                    .orElseGet(() -> inventoryRepository.save(Inventory.create(menu, 0)));

            inventory.increase(orderItem.getQuantity());
            if (menu.getStatus() == MenuStatus.SOLD_OUT) {
                menu.changeStatus(MenuStatus.AVAILABLE);
            }

            cacheInvalidationService.evictMenuAndAnalyticsCaches(menu.getId());
        }
    }

    private Map<Long, Integer> aggregateRequestedQuantities(List<OrderCreateItemRequest> items) {
        Map<Long, Integer> requestedQuantities = new LinkedHashMap<>();

        for (OrderCreateItemRequest item : items) {
            requestedQuantities.merge(item.menuId(), item.quantity(), Integer::sum);
        }

        return requestedQuantities;
    }

    private Map<Long, Inventory> loadInventories(Collection<Long> menuIds) {
        return inventoryRepository.findAllByMenu_IdIn(menuIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        inventory -> inventory.getMenu().getId(),
                        Function.identity()
                ));
    }

    private Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }

    private void syncMenuStatusWithQuantity(Menu menu, Integer quantity) {
        if (quantity == 0) {
            if (menu.getStatus() != MenuStatus.HIDDEN) {
                menu.changeStatus(MenuStatus.SOLD_OUT);
            }
            return;
        }

        if (menu.getStatus() == MenuStatus.SOLD_OUT) {
            menu.changeStatus(MenuStatus.AVAILABLE);
        }
    }

    private InventoryResponse toInventoryResponse(Menu menu, Inventory inventory) {
        return new InventoryResponse(
                menu.getId(),
                menu.getName(),
                menu.getCategory(),
                inventory == null ? 0 : inventory.getQuantity(),
                menu.getStatus()
        );
    }
}
