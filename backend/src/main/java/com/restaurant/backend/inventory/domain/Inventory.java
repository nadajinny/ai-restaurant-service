package com.restaurant.backend.inventory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
