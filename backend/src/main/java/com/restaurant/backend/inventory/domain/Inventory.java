package com.restaurant.backend.inventory.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.menu.domain.Menu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventories")
public class Inventory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, unique = true)
    private Menu menu;

    @Column(nullable = false)
    private Integer quantity;

    protected Inventory() {
    }

    public Menu getMenu() {
        return menu;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
