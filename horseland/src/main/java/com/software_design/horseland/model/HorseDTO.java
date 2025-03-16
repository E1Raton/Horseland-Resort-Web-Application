package com.software_design.horseland.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Data
public class HorseDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Breed is required")
    private HorseBreed breed;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    /**
     * Validates that the birth date is not more than 30 years ago.
     */
    @AssertFalse(message = "Horse cannot be older than 30 years")
    private boolean isHorseOlderThan30Years() {
        if (birthDate == null) {
            return false;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() > 30;
    }
}
