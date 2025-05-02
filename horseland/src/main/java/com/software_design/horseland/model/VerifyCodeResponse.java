package com.software_design.horseland.model;

public record VerifyCodeResponse(
        Boolean success,
        String resetToken,
        String errorMessage
) {
}
