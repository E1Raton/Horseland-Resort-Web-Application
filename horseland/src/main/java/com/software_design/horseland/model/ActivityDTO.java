package com.software_design.horseland.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
public class ActivityDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 1000, message = "Description should be at most 1000 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Set<UUID> participantsIds;

    @AssertTrue(message = "Start date should be before End date")
    private boolean isStartDateBeforeEndDate() {
        return startDate == null || startDate.isBefore(endDate);
    }
}
