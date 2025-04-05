package com.software_design.horseland.service;

import com.software_design.horseland.events.UserLoginEvent;
import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    public LoginResponse login(String username, String password) {
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            return new LoginResponse(
                    false,
                    null,
                    null,
                    "User with username " + username + " not found"
            );
        }
        User user = maybeUser.get();
        if (user.getPassword().equals(password)) {
            // Publish event
            eventPublisher.publishEvent(new UserLoginEvent(this, user));

            return new LoginResponse(true, user.getId(), user.getRole(), null);
        } else {
            return new LoginResponse(false, null, null, "Incorrect password");
        }
    }
}
