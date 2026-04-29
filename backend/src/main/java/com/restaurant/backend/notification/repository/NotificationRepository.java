package com.restaurant.backend.notification.repository;

import com.restaurant.backend.notification.domain.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByUser_Id(Long userId);

    Optional<Notification> findTopByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    List<Notification> findAllByUser_IdOrderByCreatedAtDescIdDesc(Long userId);

    Optional<Notification> findById(Long notificationId);
}
