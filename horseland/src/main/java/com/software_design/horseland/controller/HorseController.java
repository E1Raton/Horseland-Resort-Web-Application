package com.software_design.horseland.controller;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Horse;
import com.software_design.horseland.model.HorseDTO;
import com.software_design.horseland.service.HorseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class HorseController {
    private final HorseService horseService;

    @GetMapping("/horse")
    public List<Horse> getHorses() {
        return horseService.getHorses();
    }

    @GetMapping("/horse/{uuid}")
    public Horse getHorseById(@PathVariable UUID uuid) throws DatabaseValidationException {
        return horseService.getHorseById(uuid);
    }

    @GetMapping("horse/owner/{ownerId}")
    public List<Horse> getHorsesByOwnerId(@PathVariable UUID ownerId) {
        return horseService.getHorsesByOwnerId(ownerId);
    }

    @PostMapping("/horse")
    public Horse addHorse(@Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.addHorse(horseDTO);
    }

    @PostMapping("/horse/owner/{ownerId}")
    public Horse addHorseToOwner(@PathVariable UUID ownerId, @Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.addHorseToOwner(horseDTO, ownerId);
    }

    @PutMapping("/horse/{uuid}")
    public Horse updateHorse(@PathVariable UUID uuid, @Valid @RequestBody HorseDTO horseDTO) throws DatabaseValidationException {
        return horseService.updateHorse(uuid, horseDTO);
    }

    @DeleteMapping("horse/{uuid}")
    public void deleteHorse(@PathVariable UUID uuid) {
        horseService.deleteHorse(uuid);
    }
}
