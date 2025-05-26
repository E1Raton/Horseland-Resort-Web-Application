package com.software_design.horseland.service;

import com.software_design.horseland.model.*;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.JwtUtil;
import com.software_design.horseland.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final AuthTokenService authTokenService;

    private static class Token {
        String code;
        Instant expiresAt;
        Token(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    // Thread-safe map: email -> Token
    private final ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<>();

    // generate and store
    public EmailResponse generateAndSendResetCode(String email) {
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty()) {
            return new EmailResponse(false, null, "User with email \"" + email + "\" not found");
        }

        String code = String.valueOf(new Random().nextInt(900_000) + 100_000);
        Instant expiry = Instant.now().plus(10, ChronoUnit.MINUTES);
        tokens.put(email, new Token(code, expiry));

        emailService.sendVerificationCode(email, code);

        return new EmailResponse(true, code, null);
    }

    // validation function
    public VerifyCodeResponse verifyCode(String email, String submittedCode) {
        Token token = tokens.get(email);
        if (token == null || Instant.now().isAfter(token.expiresAt)) {
            tokens.remove(email);
            return new VerifyCodeResponse(false, null, "Code does not exists or has expired");
        }
        boolean ok = token.code.equals(submittedCode);
        if (ok) {
            tokens.remove(email);

            User user = userRepository.findByEmail(email).orElse(null);
            assert user != null;

            // Generate new token for the user
            String newToken = jwtUtil.createToken(user);
            AuthToken newAuthToken = new AuthToken(newToken, user.getUsername(), LocalDateTime.now().plusMinutes(5));
            authTokenService.addAuthToken(newAuthToken);

            return new VerifyCodeResponse(true, newToken, null);
        }

        return new VerifyCodeResponse(false, null, "Code is incorrect");
    }

    public ResetPasswordResponse resetPassword(String email, String newPassword) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        assert existingUser != null;

        if (newPassword == null || newPassword.isBlank()) {
            return new ResetPasswordResponse("Password is required");
        }

        if (newPassword.length() < 8) {
            return new ResetPasswordResponse("Password must be at least 8 characters long");
        }

        existingUser.setPassword(passwordUtil.hashPassword(newPassword));
        userRepository.save(existingUser);

        return new ResetPasswordResponse();
    }

    // Optionally schedule cleanup of expired entries
    @Scheduled(fixedDelay = 5 * 60 * 1000) // every 5 minutes
    public void purgeExpiredTokens() {
        Instant now = Instant.now();
        tokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt));
    }
}
