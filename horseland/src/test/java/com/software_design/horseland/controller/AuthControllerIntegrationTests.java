package com.software_design.horseland.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.software_design.horseland.model.*;
import com.software_design.horseland.repository.*;
import com.software_design.horseland.service.UserService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        userRepository.deleteAll();
        userRepository.flush();

        authTokenRepository.deleteAll();
        authTokenRepository.flush();

        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = "";

        seedDataJson = loadFixture("user_seed.json");
        List<UserDTO> usersDto = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        for (UserDTO user : usersDto) {
            userService.addUser(user);
        }

        List<User> users = userRepository.findAll();

        Activity activity = new Activity();
        activity.setName("Test Activity");
        activity.setDescription("Test Description");
        activity.setStartDate(LocalDate.now().plusDays(5));
        activity.setEndDate(LocalDate.now().plusDays(10));
        activity.addParticipant(users.get(0));
        activity.addParticipant(users.get(1));
        activity = activityRepository.save(activity);

        Notification n1 = new Notification();
        n1.setUserId(users.getFirst().getId());
        n1.setActivityId(activity.getId());
        n1.setTitle(activity.getName());
        n1.setMessage("Event in 7 days!");
        notificationRepository.save(n1);

        NotificationPreference pref1 = new NotificationPreference();
        pref1.setUserId(users.getFirst().getId());
        pref1.setActivityId(activity.getId());
        pref1.setActive(true);
        notificationPreferenceRepository.save(pref1);

        NotificationPreference pref2 = new NotificationPreference();
        pref2.setUserId(users.getLast().getId());
        pref2.setActivityId(activity.getId());
        pref2.setActive(false);
        notificationPreferenceRepository.save(pref2);

        AuthToken authToken1 = new AuthToken();
        authToken1.setUsername(users.getFirst().getUsername());
        authToken1.setToken(jwtUtil.createToken(users.getFirst()));
        authToken1.setExpiryDate(LocalDateTime.now().plusDays(1));
        authTokenRepository.save(authToken1);

        AuthToken authToken2 = new AuthToken();
        authToken2.setUsername(users.getLast().getUsername());
        authToken2.setToken(jwtUtil.createToken(users.getLast()));
        authToken2.setExpiryDate(LocalDateTime.now().plusDays(1));
        authTokenRepository.save(authToken2);
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    @Test
    void testLoginAndNotification() throws Exception {
        List<User> users = userRepository.findAll();
        String token = authTokenRepository.findByUsername(users.getFirst().getUsername()).getToken();

        String validLoginJson = loadFixture("valid_login_john.json");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.role").value("STUDENT"));

        mockMvc.perform(get("/notification/" + users.getFirst().getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("Event in 5 days!"));
    }

    @Test
    void testLogiAndNoNotification() throws Exception {
        List<User> users = userRepository.findAll();
        String token = authTokenRepository.findByUsername(users.getLast().getUsername()).getToken();

        String validLoginJson = loadFixture("valid_login_jane.json");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.role").value("STUDENT"));

        mockMvc.perform(get("/notification/" + users.getLast().getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }
}
