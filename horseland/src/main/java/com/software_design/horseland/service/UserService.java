package com.software_design.horseland.service;

import com.software_design.horseland.exception.InputValidationException;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserCreateDTO;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserCreateDTO userDTO) throws InputValidationException {

        User userByUsername = getUserByUsername(userDTO.getUsername());
        if (userByUsername != null) {
            throw new InputValidationException("Username already used");
        }

        User userByEmail = getUserByEmail(userDTO.getEmail());
        if (userByEmail != null) {
            throw new InputValidationException("Email already used");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthDate(userDTO.getBirthDate());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());

        return userRepository.save(user);
    }

    public User updateUser(UUID uuid, User user) {
        User existingUser =
                userRepository.findById(uuid).orElseThrow(
                        () -> new IllegalStateException("User with uuid " + uuid + " not found."));
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());

        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    public User getUserByEmail(String email) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        return userByEmail.orElse(null);
    }

    public User getUserById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("User with uuid " + uuid + " not found."));
    }

    public User getUserByUsername(String username) {
        Optional<User> userByUsername = userRepository.findByUsername(username);
        return userByUsername.orElse(null);
    }
}
