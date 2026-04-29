package com.restaurant.backend.ai.repository;

import com.restaurant.backend.ai.domain.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<AiRecommendation, Long> {
}
