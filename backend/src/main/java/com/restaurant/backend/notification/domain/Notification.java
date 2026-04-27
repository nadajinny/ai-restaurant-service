package com.restaurant.backend.notification.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean read;

    protected Notification() {
    }

    public User getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return read;
    }
}
