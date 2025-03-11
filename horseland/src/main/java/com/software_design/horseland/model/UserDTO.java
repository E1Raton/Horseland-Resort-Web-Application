package com.software_design.horseland.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Data
public class UserDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name should be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name should be between 2 and 100 characters")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username should be between 4 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    /**
     * Validates that the birth date is not more than 100 years ago.
     */
    @AssertFalse(message = "User cannot be older than 100 years")
    public boolean isUserOlderThan100Years() {
        if (birthDate == null) {
            return false;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() > 100;
    }

    /**
     * Validates that the user is an adult.
     */
    @AssertFalse(message = "User should be at least 18 years old")
    public boolean isUserYoungerThan18Years() {
        if (birthDate == null) {
            return false;
        }

        return Period.between(birthDate, LocalDate.now()).getYears() < 18;
    }
}
