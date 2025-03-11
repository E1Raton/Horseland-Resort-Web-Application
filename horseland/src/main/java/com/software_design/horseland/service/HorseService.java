package com.software_design.horseland.service;

import com.software_design.horseland.model.Horse;
import com.software_design.horseland.repository.HorseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HorseService {
    private final HorseRepository horseRepository;

    public List<Horse> getHorses() {
        return horseRepository.findAll();
    }

    public Horse addHorse(Horse horse) {
        return horseRepository.save(horse);
    }

    public Horse updateHorse(UUID uuid, Horse horse) {
        Horse existingHorse =
                horseRepository.findById(uuid).orElseThrow(
                        () -> new IllegalStateException("Horse with id " + uuid + " not found"));
        existingHorse.setName(horse.getName());
        existingHorse.setBirthDate(horse.getBirthDate());
        existingHorse.setBreed(horse.getBreed());
        existingHorse.setOwner(horse.getOwner());

        return horseRepository.save(existingHorse);
    }

    public void deleteHorse(UUID uuid) {
        horseRepository.deleteById(uuid);
    }

    public Horse getHorseById(UUID uuid) {
        return horseRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Horse with id " + uuid + " not found"));
    }

    public List<Horse> getHorsesByOwnerId(UUID ownerId) {
        return horseRepository.findByOwnerId(ownerId);
    }
}
