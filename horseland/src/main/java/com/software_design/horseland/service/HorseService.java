package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Horse;
import com.software_design.horseland.model.HorseDTO;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.HorseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HorseService {
    private final HorseRepository horseRepository;
    private final UserService userService;

    public List<Horse> getHorses() {
        return horseRepository.findAll();
    }

    public Horse addHorse(HorseDTO horseDTO) throws DatabaseValidationException {
        Horse horse = new Horse();
        horse.setName(horseDTO.getName());
        horse.setBreed(horseDTO.getBreed());
        horse.setBirthDate(horseDTO.getBirthDate());

        // Check if the owner exists
        User userById = userService.getUserById(horseDTO.getOwnerId());
        horse.setOwner(userById);

        return horseRepository.save(horse);
    }

    public Horse addHorseToOwner(HorseDTO horseDTO, UUID ownerId) throws DatabaseValidationException {
        Horse horse = new Horse();
        horse.setName(horseDTO.getName());
        horse.setBreed(horseDTO.getBreed());
        horse.setBirthDate(horseDTO.getBirthDate());

        // Check if owner exists
        User userById = userService.getUserById(ownerId);
        horse.setOwner(userById);

        return horseRepository.save(horse);
    }

    public Horse updateHorse(UUID uuid, HorseDTO horseDTO) throws DatabaseValidationException {
        Horse existingHorse = getHorseById(uuid);

        User userById = userService.getUserById(horseDTO.getOwnerId());

        existingHorse.setName(horseDTO.getName());
        existingHorse.setBirthDate(horseDTO.getBirthDate());
        existingHorse.setBreed(horseDTO.getBreed());
        existingHorse.setOwner(userById);

        return horseRepository.save(existingHorse);
    }

    public void deleteHorse(UUID uuid) {
        horseRepository.deleteById(uuid);
    }

    public Horse getHorseById(UUID uuid) throws DatabaseValidationException {
        return horseRepository.findById(uuid).orElseThrow(
                () -> new DatabaseValidationException("Horse with id " + uuid + " not found"));
    }

    public List<Horse> getHorsesByOwnerId(UUID ownerId) {
        return horseRepository.findByOwnerId(ownerId);
    }
}
