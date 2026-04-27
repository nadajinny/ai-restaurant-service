package com.restaurant.backend.favorite.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long menuId;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMenuId() {
        return menuId;
    }
}
