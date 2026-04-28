package com.restaurant.backend.user.service;

import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.dto.UserSummaryDto;
import com.restaurant.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String TEMPORARY_ORDER_USER_LOGIN_ID = "temporary-order-user";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserSummaryDto getSampleUser() {
        User user = userRepository.findByLoginId(TEMPORARY_ORDER_USER_LOGIN_ID)
                .orElseGet(() -> userRepository.save(User.create(
                        TEMPORARY_ORDER_USER_LOGIN_ID,
                        "temporary-password",
                        "임시 주문 사용자",
                        UserRole.USER
                )));

        return new UserSummaryDto(user.getId(), user.getLoginId(), user.getRole().name());
    }
}
