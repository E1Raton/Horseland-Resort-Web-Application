package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Horse;
import com.software_design.horseland.model.HorseBreed;
import com.software_design.horseland.model.HorseDTO;
import com.software_design.horseland.model.User;
import com.software_design.horseland.repository.HorseRepository;
import com.software_design.horseland.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HorseServiceTests {

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HorseService horseService;

    @Test
    void testGetHorses() {
        List<Horse> horses = List.of(new Horse(), new Horse());

        when(horseRepository.findAll()).thenReturn(horses);

        List<Horse> result = horseService.getHorses();

        assertEquals(2, result.size());
        verify(horseRepository, times(1)).findAll();
    }

    @Test
    void testAddHorse() throws DatabaseValidationException {
        HorseDTO horseDTO = new HorseDTO("Spirit", HorseBreed.MUSTANG, LocalDate.of(2015, 6, 1), UUID.randomUUID());
        User owner = new User();
        Horse savedHorse = new Horse();
        savedHorse.setId(UUID.randomUUID());

        when(userRepository.findById(horseDTO.getOwnerId())).thenReturn(Optional.of(owner));
        when(horseRepository.save(any(Horse.class))).thenReturn(savedHorse);

        Horse result = horseService.addHorse(horseDTO);

        assertNotNull(result.getId());
        verify(userRepository).findById(horseDTO.getOwnerId());
        verify(horseRepository).save(any(Horse.class));
    }

    @Test
    void testUpdateHorse() throws DatabaseValidationException {
        UUID horseId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Horse existingHorse = new Horse();
        existingHorse.setId(horseId);

        HorseDTO horseDTO = new HorseDTO("Bella", HorseBreed.HORRO, LocalDate.of(2014, 5, 10), ownerId);
        User owner = new User();
        owner.setId(ownerId);

        Horse updatedHorse = new Horse();
        updatedHorse.setId(horseId);
        updatedHorse.setName("Bella");

        when(horseRepository.findById(horseId)).thenReturn(Optional.of(existingHorse));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(horseRepository.save(existingHorse)).thenReturn(updatedHorse);

        Horse result = horseService.updateHorse(horseId, horseDTO);

        assertEquals("Bella", result.getName());
        verify(horseRepository).findById(horseId);
        verify(horseRepository).save(existingHorse);
    }

    @Test
    void testUpdateHorseNotFound() {
        UUID horseId = UUID.randomUUID();
        HorseDTO horseDTO = new HorseDTO();

        when(horseRepository.findById(horseId)).thenReturn(Optional.empty());

        assertThrows(DatabaseValidationException.class, () -> horseService.updateHorse(horseId, horseDTO));
        verify(horseRepository).findById(horseId);
    }

    @Test
    void testDeleteHorse() {
        UUID horseId = UUID.randomUUID();

        doNothing().when(horseRepository).deleteById(horseId);
        horseService.deleteHorse(horseId);

        verify(horseRepository, times(1)).deleteById(horseId);
    }

    @Test
    void testGetHorseByIdFound() throws DatabaseValidationException {
        UUID horseId = UUID.randomUUID();
        Horse horse = new Horse();
        horse.setId(horseId);

        when(horseRepository.findById(horseId)).thenReturn(Optional.of(horse));

        Horse result = horseService.getHorseById(horseId);

        assertEquals(horseId, result.getId());
        verify(horseRepository).findById(horseId);
    }

    @Test
    void testGetHorseByIdNotFound() {
        UUID horseId = UUID.randomUUID();

        when(horseRepository.findById(horseId)).thenReturn(Optional.empty());

        assertThrows(DatabaseValidationException.class, () -> horseService.getHorseById(horseId));
        verify(horseRepository).findById(horseId);
    }

    @Test
    void testGetHorsesByOwnerId() {
        UUID ownerId = UUID.randomUUID();
        List<Horse> horses = List.of(new Horse());

        when(horseRepository.findByOwnerId(ownerId)).thenReturn(horses);

        List<Horse> result = horseService.getHorsesByOwnerId(ownerId);

        assertEquals(1, result.size());
        verify(horseRepository).findByOwnerId(ownerId);
    }
}
