package com.software_design.horseland.model;

public record ResetPasswordResponse(
        Boolean success,
        String errorMessage
) {
    public ResetPasswordResponse(String errorMessage) {
        this(false, errorMessage);
    }

    public ResetPasswordResponse() {
        this(true, null);
    }
}
