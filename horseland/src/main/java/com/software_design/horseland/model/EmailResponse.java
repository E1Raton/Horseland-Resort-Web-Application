package com.software_design.horseland.model;

public record EmailResponse(
        Boolean success,
        String verificationCode,
        String errorMessage
) {
}
