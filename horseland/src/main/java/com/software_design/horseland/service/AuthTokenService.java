package com.software_design.horseland.service;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.model.AuthToken;
import com.software_design.horseland.repository.AuthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    @Auditable(operation = "CREATE", entity = AuthToken.class, username = "#authToken.username")
    public void addAuthToken(AuthToken authToken) {
        authTokenRepository.save(authToken);
    }

    public AuthToken findByUsername(String username) {
        return authTokenRepository.findByUsername(username);
    }

    @Auditable(operation = "DELETE", entity = AuthToken.class, username = "#username")
    public void deleteByUsername(String username) {
        authTokenRepository.deleteByUsername(username);
    }

    @Auditable(operation = "DELETE", entity = AuthToken.class, username = "#token.username")
    public void delete(AuthToken token) {
        authTokenRepository.delete(token);
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void deleteExpiredTokens() {
        authTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
