package com.restaurant.backend.ai.controller;

import com.restaurant.backend.ai.dto.AiRecommendationDto;
import com.restaurant.backend.ai.dto.AiRecommendationRequest;
import com.restaurant.backend.ai.service.AiService;
import com.restaurant.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/recommendations/mock")
    public ApiResponse<AiRecommendationDto> recommend(@Valid @RequestBody AiRecommendationRequest request) {
        return ApiResponse.success(aiService.recommend(request));
    }
}
