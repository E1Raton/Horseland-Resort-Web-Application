package com.software_design.horseland.controller;

import com.software_design.horseland.model.*;
import com.software_design.horseland.service.AuthService;
import com.software_design.horseland.service.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest.username(), loginRequest.password());
        return loginResponse.success() ? ResponseEntity.ok(loginResponse) : ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(loginResponse);
    }

    @PostMapping("/auth/logout/{userId}")
    @PreAuthorize("#userId.toString() == authentication.details")
    @Transactional
    public boolean logout(@PathVariable UUID userId) {
        return authService.logout(userId);
    }

    @PostMapping("/auth/request-reset")
    public ResponseEntity<EmailResponse> requestResetPassword(@RequestBody EmailRequest request) {
        EmailResponse emailResponse = passwordResetService.generateAndSendResetCode(request.email());
        return emailResponse.success() ? ResponseEntity.ok(emailResponse) : ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(emailResponse);
    }

    @PostMapping("/auth/verify-code")
    public ResponseEntity<VerifyCodeResponse> verifyResetCode(@RequestBody VerifyCodeRequest request) {
        VerifyCodeResponse response = passwordResetService.verifyCode(request.email(), request.code());
        return response.success() ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = passwordResetService.resetPassword(request.email(), request.password());
        return response.success() ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }
}
