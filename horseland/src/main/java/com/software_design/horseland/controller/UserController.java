package com.software_design.horseland.controller;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.UserDTO;
import com.software_design.horseland.service.UserService;
import com.software_design.horseland.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin // don't use in production
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable UUID uuid) throws DatabaseValidationException {
        return userService.getUserById(uuid);
    }

    @GetMapping("user/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("user/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public User addUser(@Valid @RequestBody UserDTO userDTO) throws DatabaseValidationException {
        return userService.addUser(userDTO);
    }

    @PutMapping("/user/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable UUID uuid, @Valid @RequestBody UserDTO userDTO) throws DatabaseValidationException {
        return userService.updateUser(uuid, userDTO);
    }

    @DeleteMapping("/user/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
    }
}
