package com.restaurant.backend.notification.service;

import com.restaurant.backend.notification.domain.Notification;

public interface NotificationPublisher {

    void publish(Notification notification);
}
