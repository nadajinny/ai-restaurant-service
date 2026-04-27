package com.restaurant.backend.user.service;

import com.restaurant.backend.user.dto.UserSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserSummaryDto getSampleUser() {
        return new UserSummaryDto(1L, "demo-user", "USER");
    }
}
