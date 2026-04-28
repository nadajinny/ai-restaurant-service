package com.restaurant.backend.notification.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean read;

    protected Notification() {
    }

    public static Notification create(User user, NotificationType type, String content) {
        Notification notification = new Notification();
        notification.user = user;
        notification.type = type;
        notification.content = content;
        notification.read = false;
        return notification;
    }

    public void markAsRead() {
        this.read = true;
    }

    public User getUser() {
        return user;
    }

    public NotificationType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return read;
    }
}
