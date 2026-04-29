package com.restaurant.backend.order.repository;

import com.restaurant.backend.order.domain.OrderStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    long countByOrder_Id(Long orderId);

    List<OrderStatusHistory> findAllByOrder_IdOrderByCreatedAtAscIdAsc(Long orderId);
}
