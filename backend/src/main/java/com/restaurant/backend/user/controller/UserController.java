package com.restaurant.backend.user.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.user.dto.UserSummaryDto;
import com.restaurant.backend.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sample")
    public ApiResponse<UserSummaryDto> getSampleUser() {
        return ApiResponse.success(userService.getSampleUser());
    }
}
