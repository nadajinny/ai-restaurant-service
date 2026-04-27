package com.restaurant.backend.analytics.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "analytics_metrics")
public class AnalyticsMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metricName;
    private String metricValue;

    public Long getId() {
        return id;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getMetricValue() {
        return metricValue;
    }
}
