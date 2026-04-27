package com.restaurant.backend.order.repository;

import com.restaurant.backend.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByMenu_Id(Long menuId);
}
