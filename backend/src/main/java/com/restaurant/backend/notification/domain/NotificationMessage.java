package com.restaurant.backend.notification.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String type;
    private String content;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
