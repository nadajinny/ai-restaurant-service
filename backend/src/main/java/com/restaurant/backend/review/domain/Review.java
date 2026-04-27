package com.restaurant.backend.review.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private boolean aiGenerated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    protected Review() {
    }

    public User getUser() {
        return user;
    }

    public Menu getMenu() {
        return menu;
    }

    public Order getOrder() {
        return order;
    }

    public String getContent() {
        return content;
    }

    public Integer getRating() {
        return rating;
    }

    public boolean isAiGenerated() {
        return aiGenerated;
    }

    public ReviewStatus getStatus() {
        return status;
    }
}
