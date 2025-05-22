package com.software_design.horseland.controller;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Horse;
import com.software_design.horseland.model.HorseDTO;
import com.software_design.horseland.service.HorseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class HorseController {
    private final HorseService horseService;

    @GetMapping("/horse")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Horse> getHorses() {
        return horseService.getHorses();
    }

    @GetMapping("/horse/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public Horse getHorseById(@PathVariable UUID uuid) throws DatabaseValidationException {
        return horseService.getHorseById(uuid);
    }

    @GetMapping("horse/owner/{ownerId}")
    @PreAuthorize("#ownerId.toString() == authentication.details")
    public List<Horse> getHorsesByOwnerId(@PathVariable UUID ownerId) {
        return horseService.getHorsesByOwnerId(ownerId);
    }

    @PostMapping("/horse")
    @PreAuthorize("hasRole('ADMIN')")
    public Horse addHorse(@Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.addHorse(horseDTO);
    }

    @PostMapping("/horse/owner/{ownerId}")
    @PreAuthorize("#ownerId.toString() == authentication.details")
    public Horse addHorseToOwner(@PathVariable UUID ownerId, @Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.addHorseToOwner(horseDTO, ownerId);
    }

    @PutMapping("/horse/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public Horse updateHorse(@PathVariable UUID uuid, @Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.updateHorse(uuid, horseDTO);
    }

    @DeleteMapping("horse/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public void deleteHorse(@PathVariable UUID uuid) {
        horseService.deleteHorse(uuid);
    }
}
