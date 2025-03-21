package com.software_design.horseland.model;

public record LoginResponse(
        Boolean success,
        Role role,
        String errorMessage
) {
}
