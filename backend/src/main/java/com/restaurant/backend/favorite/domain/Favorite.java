package com.restaurant.backend.favorite.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorite_user_menu", columnNames = {"user_id", "menu_id"})
        }
)
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    protected Favorite() {
    }

    public static Favorite create(User user, Menu menu) {
        Favorite favorite = new Favorite();
        favorite.user = user;
        favorite.menu = menu;
        return favorite;
    }

    public User getUser() {
        return user;
    }

    public Menu getMenu() {
        return menu;
    }
}
