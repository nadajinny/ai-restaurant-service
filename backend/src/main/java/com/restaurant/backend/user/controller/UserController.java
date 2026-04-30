package com.restaurant.backend.user.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.user.dto.AuthenticatedUserDto;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자", description = "사용자 정보 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 조회", description = "로그인한 사용자의 기본 정보를 조회합니다.")
    public ApiResponse<AuthenticatedUserDto> getCurrentUser(Authentication authentication) {
        return ApiResponse.success(currentUserService.getCurrentUserSummary(authentication));
    }
}
