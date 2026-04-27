package com.restaurant.backend.menu.service;

import com.restaurant.backend.menu.dto.MenuSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    public MenuSummaryDto getSampleMenu() {
        return new MenuSummaryDto(1L, "Kimchi Stew", 9000);
    }
}
