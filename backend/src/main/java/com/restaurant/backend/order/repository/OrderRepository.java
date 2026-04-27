package com.restaurant.backend.order.repository;

import com.restaurant.backend.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
