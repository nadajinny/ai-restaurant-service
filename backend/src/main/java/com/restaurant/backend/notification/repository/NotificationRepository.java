package com.restaurant.backend.notification.repository;

import com.restaurant.backend.notification.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationMessage, Long> {
}
