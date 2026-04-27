package com.restaurant.backend.analytics.service;

import com.restaurant.backend.analytics.dto.AnalyticsSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    public AnalyticsSummaryDto getSampleAnalytics() {
        return new AnalyticsSummaryDto("todaySales", "120000");
    }
}
