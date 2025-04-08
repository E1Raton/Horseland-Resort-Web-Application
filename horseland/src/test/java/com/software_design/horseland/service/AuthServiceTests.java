package com.software_design.horseland.service;

import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.Role;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.UserRepository;
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

    @InjectMocks
    private AuthService authService;

    @Test
    void testLoginSuccess() {
        String username = "username";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
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
        user.setPassword("wrong password");

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
