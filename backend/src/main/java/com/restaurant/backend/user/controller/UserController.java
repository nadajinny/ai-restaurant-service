package com.restaurant.backend.user.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.user.dto.AuthenticatedUserDto;
import com.restaurant.backend.user.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public ApiResponse<AuthenticatedUserDto> getCurrentUser(Authentication authentication) {
        return ApiResponse.success(currentUserService.getCurrentUserSummary(authentication));
    }
}
