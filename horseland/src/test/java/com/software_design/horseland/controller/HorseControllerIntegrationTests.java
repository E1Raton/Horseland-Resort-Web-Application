package com.software_design.horseland.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.software_design.horseland.model.*;
import com.software_design.horseland.repository.HorseRepository;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class HorseControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private User john;
    private Horse bella;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        horseRepository.deleteAll();
        userRepository.deleteAll();

        john = new User();
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setBirthDate(LocalDate.of(2001, 10, 21));
        john.setUsername("john_doe");
        john.setEmail("johndoe@example.com");
        john.setPassword("password123");
        john.setRole(Role.STUDENT);

        john = userRepository.save(john);

        bella = new Horse();
        bella.setName("Bella");
        bella.setBreed(HorseBreed.AXIOS);
        bella.setBirthDate(LocalDate.of(2015, 5, 10));
        bella.setOwner(john);

        bella = horseRepository.save(bella);
    }

    @Test
    void testGetHorses() throws Exception {
        mockMvc.perform(get("/horse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bella"));
    }

    @Test
    void testGetHorseById() throws Exception {
        mockMvc.perform(get("/horse/" + bella.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bella"));
    }

    @Test
    void testGetHorsesByOwnerId() throws Exception {
        String token = jwtUtil.createToken(john);
        mockMvc.perform(get("/horse/owner/" + john.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].owner.id").value(john.getId().toString()));
    }

    @Test
    void testAddHorse_ValidPayload() throws Exception {
        HorseDTO horseDTO = new HorseDTO();
        horseDTO.setName("Shadowfax");
        horseDTO.setBreed(HorseBreed.SENNER);
        horseDTO.setBirthDate(LocalDate.of(2010, 4, 25));
        horseDTO.setOwnerId(john.getId());

        mockMvc.perform(post("/horse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Shadowfax"));
    }

    @Test
    void testAddHorse_InvalidPayload() throws Exception {
        HorseDTO horseDTO = new HorseDTO();
        horseDTO.setName("S");
        horseDTO.setBreed(null); // Invalid
        horseDTO.setBirthDate(LocalDate.of(2030, 1, 1)); // Future
        horseDTO.setOwnerId(null); // Missing owner

        mockMvc.perform(post("/horse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testAddHorseToOwner() throws Exception {
        HorseDTO horseDTO = new HorseDTO();
        horseDTO.setName("Star");
        horseDTO.setBreed(HorseBreed.HORRO);
        horseDTO.setBirthDate(LocalDate.of(2013, 3, 15));
        horseDTO.setOwnerId(john.getId());

        String token = jwtUtil.createToken(john);

        mockMvc.perform(post("/horse/owner/" + john.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horseDTO))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Star"))
                .andExpect(jsonPath("$.owner.id").value(john.getId().toString()));
    }

    @Test
    void testUpdateHorse() throws Exception {
        HorseDTO updatedHorse = new HorseDTO();
        updatedHorse.setName("Bella Updated");
        updatedHorse.setBreed(HorseBreed.AXIOS);
        updatedHorse.setBirthDate(LocalDate.of(2014, 4, 4));
        updatedHorse.setOwnerId(john.getId());

        mockMvc.perform(put("/horse/" + bella.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedHorse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bella Updated"))
                .andExpect(jsonPath("$.breed").value("AXIOS"));
    }

    @Test
    void testDeleteHorse() throws Exception {
        UUID idToDelete = bella.getId();

        mockMvc.perform(delete("/horse/" + idToDelete))
                .andExpect(status().isOk());

        assertThat(horseRepository.findById(idToDelete)).isEmpty();
    }
}
