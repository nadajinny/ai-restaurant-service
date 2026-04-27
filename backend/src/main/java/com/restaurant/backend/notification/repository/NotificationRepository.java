package com.restaurant.backend.notification.repository;

import com.restaurant.backend.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
