package com.software_design.horseland.service;

import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.Role;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.AuthTokenRepository;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.JwtUtil;
import com.software_design.horseland.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthTokenService authTokenService;

    @Test
    void testLoginSuccess() {
        String username = "username";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.hashPassword(password));
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordUtil.checkPassword(password, user.getPassword())).thenReturn(true);
        when(authTokenService.findByUsername(username)).thenReturn(null);
        LoginResponse result = authService.login(username, password);

        assertTrue(result.success());
        assertEquals(Role.ADMIN, result.role());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoginIncorrectPassword() {
        String username = "username";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.hashPassword("wrong password"));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        LoginResponse result = authService.login(username, password);

        assertFalse(result.success());
        assertEquals("Incorrect password", result.errorMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoginUsernameNotFound() {
        String username = "username";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        LoginResponse result = authService.login(username, password);

        assertFalse(result.success());
        assertEquals("User with username " + username + " not found" , result.errorMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
}
