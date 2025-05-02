package com.software_design.horseland.controller;

import com.software_design.horseland.model.*;
import com.software_design.horseland.service.AuthService;
import com.software_design.horseland.service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest.username(), loginRequest.password());
        if (loginResponse.success()) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(loginResponse);
        }
    }

    @PostMapping("/auth/request-reset")
    public ResponseEntity<EmailResponse> requestResetPassword(@RequestBody EmailRequest request) {
        EmailResponse emailResponse = passwordResetService.generateAndSendResetCode(request.email());
        if (emailResponse.success()) {
            return ResponseEntity.ok(emailResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(emailResponse);
        }
    }

    @PostMapping("/auth/verify-code")
    public ResponseEntity<VerifyCodeResponse> verifyResetCode(@RequestBody VerifyCodeRequest request) {
        VerifyCodeResponse response = passwordResetService.verifyCode(request.email(), request.code());
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
        }
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = passwordResetService.resetPassword(request.email(), request.password());
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
        }
    }
}
