package com.software_design.horseland.controller;

import com.software_design.horseland.exception.InputValidationException;
import com.software_design.horseland.model.UserCreateDTO;
import com.software_design.horseland.service.UserService;
import com.software_design.horseland.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin // don't use in production
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user/{uuid}")
    public User getUserById(@PathVariable UUID uuid) {
        return userService.getUserById(uuid);
    }

    @GetMapping("user/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("user/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping("/user")
    public User addUser(@Valid @RequestBody UserCreateDTO userDTO) throws InputValidationException {
        return userService.addUser(userDTO);
    }

    @PutMapping("/user/{uuid}")
    public User updateUser(@PathVariable UUID uuid, @RequestBody User user) {
        return userService.updateUser(uuid, user);
    }

    @DeleteMapping("/user/{uuid}")
    public void deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
    }
}
