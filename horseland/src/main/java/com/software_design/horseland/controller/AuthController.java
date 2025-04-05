package com.software_design.horseland.controller;

import com.software_design.horseland.model.LoginRequest;
import com.software_design.horseland.model.LoginResponse;
import com.software_design.horseland.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService loginService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = loginService.login(loginRequest.username(), loginRequest.password());
        if (loginResponse.success()) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(loginResponse);
        }
    }
}
