package com.restaurant.backend.user.repository;

import com.restaurant.backend.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserProfile, Long> {
}
