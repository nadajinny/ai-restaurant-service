package com.restaurant.backend.order.repository;

import com.restaurant.backend.order.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    Optional<Order> findById(Long id);
}
