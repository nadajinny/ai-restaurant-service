package com.restaurant.backend.common.controller;

import com.restaurant.backend.common.dto.HealthCheckResponse;
import com.restaurant.backend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "시스템", description = "시스템 상태 확인 API")
public class HealthController {

    @GetMapping
    @Operation(summary = "헬스 체크", description = "백엔드 서비스의 기본 상태를 확인합니다.")
    public ApiResponse<HealthCheckResponse> health() {
        HealthCheckResponse data = new HealthCheckResponse("UP", "backend");
        return ApiResponse.success("헬스 체크가 성공했습니다.", data);
    }
}
