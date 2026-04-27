package com.restaurant.backend.ai.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_recommendations")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;
    private String name;
    private String reason;

    public Long getId() {
        return id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }
}
