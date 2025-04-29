package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserDTO;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordUtil passwordUtil;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserDTO userDTO) throws DatabaseValidationException {

        if (isEmailAlreadyUsed(userDTO.getEmail(), null)) {
            throw new DatabaseValidationException("Email already used");
        }
        else if (isUsernameAlreadyUsed(userDTO.getUsername(), null)) {
            throw new DatabaseValidationException("Username already used");
        }
        else {
            User user = new User();
            updateUserFields(user, userDTO);
            return userRepository.save(user);
        }
    }

    private boolean isEmailAlreadyUsed(String email, UUID uuid) {
        return userRepository.findByEmail(email)
                .map(user -> !user.getId().equals(uuid))
                .orElse(false);
    }

    private boolean isUsernameAlreadyUsed(String username, UUID uuid) {
        return userRepository.findByUsername(username)
                .map(user -> !user.getId().equals(uuid))
                .orElse(false);
    }

    private void updateUserFields(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthDate(userDTO.getBirthDate());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordUtil.hashPassword(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
    }

    public User updateUser(UUID uuid, UserDTO userDTO) throws DatabaseValidationException {
        User existingUser = userRepository.findById(uuid)
                .orElseThrow(() -> new DatabaseValidationException("User with uuid " + uuid + " not found"));

        if (isEmailAlreadyUsed(userDTO.getEmail(), uuid)) {
            throw new DatabaseValidationException("Email already used");
        }
        else if (isUsernameAlreadyUsed(userDTO.getUsername(), uuid)) {
            throw new DatabaseValidationException("Username already used");
        }
        else {
            updateUserFields(existingUser, userDTO);
            return userRepository.save(existingUser);
        }
    }

    public void deleteUser(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserById(UUID uuid) throws DatabaseValidationException {
        return userRepository.findById(uuid).orElseThrow(
                () -> new DatabaseValidationException("User with uuid " + uuid + " not found."));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
