package com.restaurant.backend.common.controller;

import com.restaurant.backend.common.dto.HealthCheckResponse;
import com.restaurant.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<HealthCheckResponse> health() {
        HealthCheckResponse data = new HealthCheckResponse("UP", "backend");
        return ApiResponse.success("헬스 체크가 성공했습니다.", data);
    }
}
