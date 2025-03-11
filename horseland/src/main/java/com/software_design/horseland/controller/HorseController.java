package com.software_design.horseland.controller;

import com.software_design.horseland.model.Horse;
import com.software_design.horseland.service.HorseService;
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
    public Horse getHorseById(@PathVariable UUID uuid) {
        return horseService.getHorseById(uuid);
    }

    @GetMapping("horse/owner/{ownerId}")
    public List<Horse> getHorsesByOwnerId(@PathVariable UUID ownerId) {
        return horseService.getHorsesByOwnerId(ownerId);
    }

    @PostMapping("/horse")
    public Horse addHorse(@RequestBody Horse horse) {
        return horseService.addHorse(horse);
    }

    @PutMapping("/horse/{uuid}")
    public Horse updateHorse(@PathVariable UUID uuid, @RequestBody Horse horse) {
        return horseService.updateHorse(uuid, horse);
    }

    @DeleteMapping("horse/{uuid}")
    public void deleteHorse(@PathVariable UUID uuid) {
        horseService.deleteHorse(uuid);
    }
}
