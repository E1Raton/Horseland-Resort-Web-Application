package com.software_design.horseland.model;

public record LoginRequest(
        String username,
        String password
) {
}
