package com.software_design.horseland.service;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.events.UserLoginEvent;
import com.software_design.horseland.model.AuthToken;
import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.AuthTokenRepository;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.JwtUtil;
import com.software_design.horseland.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    private final ApplicationEventPublisher eventPublisher;

    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    @Auditable(operation = "LOG IN", username = "#username")
    public LoginResponse login(String username, String password) {
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            return new LoginResponse("User with username " + username + " not found");
        }

        User user = maybeUser.get();

        if (!passwordUtil.checkPassword(password, user.getPassword())) {
            return new LoginResponse("Incorrect password");
        }

        eventPublisher.publishEvent(new UserLoginEvent(this, user));

        AuthToken existingToken = authTokenService.findByUsername(user.getUsername());

        String token;
        LocalDateTime now = LocalDateTime.now();

        if (existingToken != null && existingToken.getExpiryDate().isAfter(now)) {
            // Reuse valid token
            token = existingToken.getToken();
        } else {
            // Create new token
            token = jwtUtil.createToken(user);
            LocalDateTime expiration = now.plusMinutes(1); // e.g., ? validity

            AuthToken newToken = new AuthToken(token, user.getUsername(), expiration);

            // Optional: delete old token first
            if (existingToken != null) {
                authTokenService.delete(existingToken);
            }

            authTokenService.addAuthToken(newToken);
        }

        return new LoginResponse(user.getId(), user.getRole(), token);
    }

    public boolean logout(UUID userId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isEmpty()) return false;

        User user = maybeUser.get();
        authTokenService.deleteByUsername(user.getUsername());

        return true;
    }
}
