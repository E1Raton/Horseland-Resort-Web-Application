package com.software_design.horseland.model;

import java.util.UUID;

public record LoginResponse(
        Boolean success,
        UUID userId,
        Role role,
        String errorMessage
) {
}
