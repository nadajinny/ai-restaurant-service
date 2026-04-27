package com.restaurant.backend.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String name;
    private String role;

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
