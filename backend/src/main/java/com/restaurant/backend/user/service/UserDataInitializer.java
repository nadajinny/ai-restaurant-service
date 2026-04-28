package com.restaurant.backend.user.service;

import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureUser("user01", "password", "일반 사용자", UserRole.USER);
        ensureUser("admin01", "password", "관리자 사용자", UserRole.ADMIN);
    }

    private void ensureUser(String loginId, String rawPassword, String name, UserRole role) {
        String encodedPassword = passwordEncoder.encode(rawPassword);

        userRepository.findByLoginId(loginId)
                .ifPresentOrElse(
                        user -> {
                            user.synchronizeProfile(encodedPassword, name, role);
                            userRepository.save(user);
                        },
                        () -> userRepository.save(User.create(loginId, encodedPassword, name, role))
                );
    }
}
