package com.restaurant.backend.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.restaurant.backend.user.domain.User;
import com.restaurant.backend.user.domain.UserRole;
import com.restaurant.backend.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserDataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDataInitializer userDataInitializer;

    @BeforeEach
    void setUp() {
        userDataInitializer = new UserDataInitializer(userRepository, passwordEncoder);
    }

    @Test
    void runCreatesDefaultUsersWhenMissing() throws Exception {
        given(userRepository.findByLoginId("user01")).willReturn(Optional.empty());
        given(userRepository.findByLoginId("admin01")).willReturn(Optional.empty());
        given(passwordEncoder.encode("password")).willReturn("encoded-password");

        userDataInitializer.run(new DefaultApplicationArguments(new String[0]));

        verify(passwordEncoder, times(2)).encode("password");
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void runDoesNotOverwriteExistingUsers() throws Exception {
        given(userRepository.findByLoginId("user01"))
                .willReturn(Optional.of(User.create("user01", "saved-password", "저장된 사용자", UserRole.USER)));
        given(userRepository.findByLoginId("admin01"))
                .willReturn(Optional.of(User.create("admin01", "saved-password", "저장된 관리자", UserRole.ADMIN)));

        userDataInitializer.run(new DefaultApplicationArguments(new String[0]));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }
}
