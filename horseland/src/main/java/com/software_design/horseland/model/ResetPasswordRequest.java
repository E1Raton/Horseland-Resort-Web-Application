package com.software_design.horseland.model;

public record ResetPasswordRequest(String email, String code, String password) {
}
