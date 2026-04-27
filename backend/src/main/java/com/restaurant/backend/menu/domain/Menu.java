package com.restaurant.backend.menu.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.favorite.domain.Favorite;
import com.restaurant.backend.inventory.domain.Inventory;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.review.domain.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private Integer cookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MenuStatus status;

    @OneToMany(mappedBy = "menu")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "menu")
    private Inventory inventory;

    protected Menu() {
    }

    public static Menu create(
            String name,
            String category,
            Integer price,
            String description,
            String imageUrl,
            Integer cookingTime,
            MenuStatus status
    ) {
        Menu menu = new Menu();
        menu.name = name;
        menu.category = category;
        menu.price = price;
        menu.description = description;
        menu.imageUrl = imageUrl;
        menu.cookingTime = cookingTime;
        menu.status = status;
        return menu;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Integer getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public MenuStatus getStatus() {
        return status;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
