package com.restaurant.backend.menu.service;

import com.restaurant.backend.inventory.domain.Inventory;
import com.restaurant.backend.inventory.repository.InventoryRepository;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MenuDataInitializer implements ApplicationRunner {

    private final MenuRepository menuRepository;
    private final InventoryRepository inventoryRepository;

    public MenuDataInitializer(MenuRepository menuRepository, InventoryRepository inventoryRepository) {
        this.menuRepository = menuRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (menuRepository.count() > 0) {
            backfillMissingInventories();
            return;
        }

        List<Menu> menus = menuRepository.saveAll(List.of(
                Menu.create(
                        "김치볶음밥",
                        "한식",
                        8500,
                        "매콤하게 볶아낸 김치와 계란프라이가 올라간 든든한 한 그릇 메뉴입니다.",
                        "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=900&q=80",
                        12,
                        MenuStatus.AVAILABLE
                ),
                Menu.create(
                        "차슈 라멘",
                        "일식",
                        11000,
                        "진한 돈코츠 육수에 차슈와 반숙란을 더한 대표 라멘 메뉴입니다.",
                        "https://images.unsplash.com/photo-1557872943-16a5ac26437e?auto=format&fit=crop&w=900&q=80",
                        14,
                        MenuStatus.AVAILABLE
                ),
                Menu.create(
                        "쉬림프 로제 파스타",
                        "양식",
                        12900,
                        "새우와 로제 소스를 곁들인 부드럽고 진한 파스타입니다.",
                        "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?auto=format&fit=crop&w=900&q=80",
                        16,
                        MenuStatus.AVAILABLE
                ),
                Menu.create(
                        "훈제오리 샐러드",
                        "샐러드",
                        9800,
                        "신선한 채소와 훈제오리를 담은 가벼운 한 끼용 샐러드입니다.",
                        "https://images.unsplash.com/photo-1546793665-c74683f339c1?auto=format&fit=crop&w=900&q=80",
                        8,
                        MenuStatus.SOLD_OUT
                )
        ));

        inventoryRepository.saveAll(List.of(
                Inventory.create(menus.get(0), 25),
                Inventory.create(menus.get(1), 18),
                Inventory.create(menus.get(2), 14),
                Inventory.create(menus.get(3), 0)
        ));
    }

    private void backfillMissingInventories() {
        for (Menu menu : menuRepository.findAll()) {
            inventoryRepository.findByMenu_Id(menu.getId())
                    .orElseGet(() -> inventoryRepository.save(Inventory.create(menu, 0)));
        }
    }
}
