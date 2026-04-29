package com.restaurant.backend.user.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.dto.AuthenticatedUserDto;
import com.restaurant.backend.user.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Long getCurrentUserId(Authentication authentication) {
        return getCurrentUser(authentication).getId();
    }

    @Transactional(readOnly = true)
    public AuthenticatedUserDto getCurrentUserSummary(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return new AuthenticatedUserDto(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getRole().name()
        );
    }
}
