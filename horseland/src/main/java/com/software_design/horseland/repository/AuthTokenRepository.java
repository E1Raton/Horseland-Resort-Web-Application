package com.software_design.horseland.repository;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    AuthToken findByToken(String token);
    AuthToken findByUsername(String username);
    void deleteByUsername(String username);
    void deleteByExpiryDateBefore(LocalDateTime now);
}
