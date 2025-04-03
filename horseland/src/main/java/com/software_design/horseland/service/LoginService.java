package com.software_design.horseland.service;

import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

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
            return new LoginResponse(true, user.getId(), user.getRole(), null);
        } else {
            return new LoginResponse(false, null, null, "Incorrect password");
        }
    }
}
