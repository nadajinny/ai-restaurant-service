package com.restaurant.backend.user.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.favorite.domain.Favorite;
import com.restaurant.backend.notification.domain.Notification;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.review.domain.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    protected User() {
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
