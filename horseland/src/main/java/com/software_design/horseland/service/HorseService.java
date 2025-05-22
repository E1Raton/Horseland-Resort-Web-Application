package com.software_design.horseland.service;

import com.software_design.horseland.annotation.Auditable;
import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Horse;
import com.software_design.horseland.model.HorseDTO;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.HorseRepository;
import com.software_design.horseland.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HorseService {
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    public List<Horse> getHorses() {
        return horseRepository.findAll();
    }

    @Auditable(operation = "CREATE", entity = Horse.class)
    public Horse addHorse(HorseDTO horseDTO) throws DatabaseValidationException {
        return Optional.of(horseDTO)
                .map(this::createHorseFromDTO)
                .map(horseRepository::save)
                .orElseThrow(() -> new DatabaseValidationException("Failed to add horse"));
    }

    @Auditable(operation = "CREATE", entity = Horse.class)
    public Horse addHorseToOwner(HorseDTO horseDTO, UUID ownerId) throws DatabaseValidationException {
        return Optional.of(horseDTO)
                .map(dto -> createHorseWithOwner(dto, ownerId))
                .map(horseRepository::save)
                .orElseThrow(() -> new DatabaseValidationException("Failed to add horse to owner"));
    }

    @Auditable(operation = "UPDATE", entity = Horse.class)
    public Horse updateHorse(UUID uuid, HorseDTO horseDTO) throws DatabaseValidationException {
        return horseRepository.findById(uuid)
                .map(existing -> updateHorseFields(existing, horseDTO))
                .map(horseRepository::save)
                .orElseThrow(() -> new DatabaseValidationException("Horse with id " + uuid + " not found"));
    }

    @Auditable(operation = "DELETE", entity = Horse.class)
    public void deleteHorse(UUID uuid) {
        Optional.ofNullable(uuid)
                .ifPresent(horseRepository::deleteById);
    }

    public Horse getHorseById(UUID uuid) throws DatabaseValidationException {
        return horseRepository.findById(uuid)
                .orElseThrow(() -> new DatabaseValidationException("Horse with id " + uuid + " not found"));
    }

    public List<Horse> getHorsesByOwnerId(UUID ownerId) {
        return Optional.ofNullable(ownerId)
                .map(horseRepository::findByOwnerId)
                .orElse(List.of());
    }

    // ---------- Private helpers ----------

    private Horse createHorseFromDTO(HorseDTO dto) {
        Horse horse = new Horse();
        horse.setName(dto.getName());
        horse.setBreed(dto.getBreed());
        horse.setBirthDate(dto.getBirthDate());
        horse.setOwner(getOwnerOrThrow(dto.getOwnerId()));
        return horse;
    }

    private Horse createHorseWithOwner(HorseDTO dto, UUID ownerId) {
        Horse horse = new Horse();
        horse.setName(dto.getName());
        horse.setBreed(dto.getBreed());
        horse.setBirthDate(dto.getBirthDate());
        horse.setOwner(getOwnerOrThrow(ownerId));
        return horse;
    }

    private Horse updateHorseFields(Horse horse, HorseDTO dto) {
        horse.setName(dto.getName());
        horse.setBirthDate(dto.getBirthDate());
        horse.setBreed(dto.getBreed());
        horse.setOwner(getOwnerOrThrow(dto.getOwnerId()));
        return horse;
    }

    private User getOwnerOrThrow(UUID ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User with id " + ownerId + " not found"));
    }
}
