package com.software_design.horseland.model;

import java.util.UUID;

public record LoginResponse(
        Boolean success,
        UUID userId,
        Role role,
        String errorMessage,
        String token
) {
    public LoginResponse(String errorMessage) {
        this(false, null, null, errorMessage, null);
    }

    public LoginResponse(UUID uuid, Role role, String token) {
        this(true, uuid, role, null, token);
    }
}
