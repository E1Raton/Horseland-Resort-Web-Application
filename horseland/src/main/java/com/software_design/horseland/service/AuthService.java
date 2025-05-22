package com.software_design.horseland.service;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.events.UserLoginEvent;
import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.JwtUtil;
import com.software_design.horseland.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    @Auditable(operation = "LOG IN", username = "#username")
    public LoginResponse login(String username, String password) {
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            return new LoginResponse(
                    "User with username " + username + " not found"
            );
        }
        User user = maybeUser.get();
        if (passwordUtil.checkPassword(password, user.getPassword())) {
            // Publish event
            eventPublisher.publishEvent(new UserLoginEvent(this, user));
            String token = jwtUtil.createToken(user);
            return new LoginResponse(user.getId(), user.getRole(), token);
        } else {
            return new LoginResponse("Incorrect password");
        }
    }
}
