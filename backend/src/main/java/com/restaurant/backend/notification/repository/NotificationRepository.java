package com.restaurant.backend.notification.repository;

import com.restaurant.backend.notification.domain.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByUser_Id(Long userId);

    Optional<Notification> findTopByUser_IdOrderByCreatedAtDescIdDesc(Long userId);
}
