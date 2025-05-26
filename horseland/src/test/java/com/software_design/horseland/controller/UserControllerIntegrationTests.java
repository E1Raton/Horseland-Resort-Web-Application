package com.software_design.horseland.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.software_design.horseland.model.AuthToken;
import com.software_design.horseland.model.Role;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserDTO;
import com.software_design.horseland.repository.*;
import com.software_design.horseland.service.UserService;
import com.software_design.horseland.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        // Ensure the ObjectMapper is configured with JavaTimeModule for tests
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() throws Exception {
        notificationRepository.deleteAll();
        notificationRepository.flush();

        notificationPreferenceRepository.deleteAll();
        notificationPreferenceRepository.flush();

        activityRepository.deleteAll();
        activityRepository.flush();

        horseRepository.deleteAll();
        horseRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();

        authTokenRepository.deleteAll();
        authTokenRepository.flush();

        seedDatabase();
    }

    private AuthToken adminToken;

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("user_seed.json");
        List<UserDTO> users = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        for (UserDTO user : users) {
            userService.addUser(user);
        }

        adminToken = getAdminToken();
        authTokenRepository.save(adminToken);
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    private AuthToken getAdminToken() {
        User user = new User(
                UUID.randomUUID(),
                "James",
                "Bond",
                LocalDate.of(2000, 1, 30),
                "bond007", "bond@email.com",
                "12345678",
                Role.ADMIN);

        return new AuthToken(jwtUtil.createToken(user), user.getUsername(), LocalDateTime.now().plusDays(1));
    }

    @Test
    void testGetUsers() throws Exception {

        String token = adminToken.getToken();

        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].firstName",
                        Matchers.containsInAnyOrder("John", "Jane")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].lastName",
                        Matchers.containsInAnyOrder("Doe", "Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].birthDate",
                        Matchers.containsInAnyOrder("2001-10-21", "2002-07-12")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].username",
                        Matchers.containsInAnyOrder("john_doe", "jane_doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email",
                        Matchers.containsInAnyOrder(
                                "johndoe@company.com", "janedoe@company.com"
                        )))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].role",
                        Matchers.containsInAnyOrder("STUDENT", "STUDENT")));
    }

    @Test
    void testAddUser_ValidPayload() throws Exception {
        String validUserJson = loadFixture("valid_user.json");

        String token = adminToken.getToken();

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.lastName").value("Bond"))
                .andExpect(jsonPath("$.birthDate").value("1982-10-10"))
                .andExpect(jsonPath("$.username").value("bond007"))
                .andExpect(jsonPath("$.email").value("bond@email.com"))
                .andExpect(jsonPath("$.role").value("INSTRUCTOR"));
    }

    @Test
    void testAddUser_InvalidPayload() throws Exception {
        String invalidUserJson = loadFixture("invalid_user.json");

        String token = adminToken.getToken();

        mockMvc.perform(post("/user") // Ensure the URL is correct, based on your response "/user"
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName")
                        .value("First name should be between 2 and 100 characters"))
                .andExpect(jsonPath("$.errors.password")
                        .value("Password must be at least 8 characters long"))
                .andExpect(jsonPath("$.errors.role")
                        .value("Role is required"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Invalid email format"))
                .andExpect(jsonPath("$.errors.userOlderThan100Years")
                        .value("User cannot be older than 100 years"))
                .andExpect(jsonPath("$.errors.username")
                        .value("Username should be between 4 and 50 characters"));
    }

}
