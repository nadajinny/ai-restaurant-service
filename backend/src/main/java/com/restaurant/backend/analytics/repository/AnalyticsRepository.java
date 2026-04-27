package com.restaurant.backend.analytics.repository;

import com.restaurant.backend.analytics.domain.AnalyticsMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsRepository extends JpaRepository<AnalyticsMetric, Long> {
}
