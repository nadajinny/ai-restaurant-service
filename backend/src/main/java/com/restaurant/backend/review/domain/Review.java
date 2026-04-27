package com.restaurant.backend.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long menuId;
    private Integer rating;
    private String content;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }
}
