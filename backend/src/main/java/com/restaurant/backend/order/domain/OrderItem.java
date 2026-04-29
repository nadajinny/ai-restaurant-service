package com.restaurant.backend.order.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.menu.domain.Menu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer itemPrice;

    protected OrderItem() {
    }

    public static OrderItem create(Order order, Menu menu, Integer quantity, Integer itemPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.order = order;
        orderItem.menu = menu;
        orderItem.quantity = quantity;
        orderItem.itemPrice = itemPrice;
        return orderItem;
    }

    public Order getOrder() {
        return order;
    }

    public Menu getMenu() {
        return menu;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getItemPrice() {
        return itemPrice;
    }
}
