package com.restaurant.backend.user.service;

import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.config.JwtTokenProvider;
import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.dto.AuthenticatedUserDto;
import com.restaurant.backend.user.dto.LoginRequest;
import com.restaurant.backend.user.dto.LoginResponse;
import com.restaurant.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return new LoginResponse(
                jwtTokenProvider.generateAccessToken(user),
                "Bearer",
                jwtTokenProvider.getAccessTokenExpiresAt().toString(),
                new AuthenticatedUserDto(
                        user.getId(),
                        user.getLoginId(),
                        user.getName(),
                        user.getRole().name()
                )
        );
    }
}
