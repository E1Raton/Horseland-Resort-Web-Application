package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserDTO;
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

    public User addUser(UserDTO userDTO) throws DatabaseValidationException {

        User userByUsername = getUserByUsername(userDTO.getUsername());
        if (userByUsername != null) {
            throw new DatabaseValidationException("Username already used");
        }

        User userByEmail = getUserByEmail(userDTO.getEmail());
        if (userByEmail != null) {
            throw new DatabaseValidationException("Email already used");
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

    public User updateUser(UUID uuid, UserDTO userDTO) throws DatabaseValidationException {
        User existingUser = getUserById(uuid);

        if (existingUser == null) {
            throw new DatabaseValidationException("User with uuid " + uuid + " not found");
        }

        User userByEmail = getUserByEmail(userDTO.getEmail());
        if (userByEmail != null && !uuid.equals(userByEmail.getId())) {
            throw new DatabaseValidationException("Email already used");
        }

        User userByUsername = getUserByUsername(userDTO.getUsername());
        if (userByUsername != null && !uuid.equals(userByUsername.getId())) {
            throw new DatabaseValidationException("Username already used");
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setBirthDate(userDTO.getBirthDate());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setRole(userDTO.getRole());

        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    public User getUserByEmail(String email) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        return userByEmail.orElse(null);
    }

    public User getUserById(UUID uuid) throws DatabaseValidationException {
        return userRepository.findById(uuid).orElseThrow(
                () -> new DatabaseValidationException("User with uuid " + uuid + " not found."));
    }

    public User getUserByUsername(String username) {
        Optional<User> userByUsername = userRepository.findByUsername(username);
        return userByUsername.orElse(null);
    }
}
