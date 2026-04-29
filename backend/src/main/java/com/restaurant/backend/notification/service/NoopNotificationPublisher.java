package com.restaurant.backend.notification.service;

import com.restaurant.backend.notification.domain.Notification;
import org.springframework.stereotype.Component;

@Component
public class NoopNotificationPublisher implements NotificationPublisher {

    @Override
    public void publish(Notification notification) {
        // TODO: 추후 WebSocket 또는 SSE 기반 실시간 전송 구현으로 대체한다.
    }
}
