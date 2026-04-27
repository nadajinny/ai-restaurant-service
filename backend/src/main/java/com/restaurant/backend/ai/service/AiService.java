package com.restaurant.backend.ai.service;

import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendationRequest;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    public AiRecommendationDto recommend(AiRecommendationRequest request) {
        return new AiRecommendationDto(
                1L,
                "Spicy Kimchi Stew",
                "Mock recommendation for request: " + request.message()
        );
    }
}
