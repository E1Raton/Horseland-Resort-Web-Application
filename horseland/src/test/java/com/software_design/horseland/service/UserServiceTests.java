package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Role;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserDTO;
import com.software_design.horseland.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUsers() {
        // given:
        List<User> users = List.of(new User(), new User());

        // when:
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getUsers();

        // then:
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
        assertEquals(users, result);
    }

    @Test
    void testAddUser() throws DatabaseValidationException {
        // given:
        UserDTO userDTO = new UserDTO("Percy", "Jackson", LocalDate.of(2003, 8, 12),
                "percy03", "percy.jackson@horseland.com", "password", Role.STUDENT);
        User userToSave = new User();
        userToSave.setFirstName("Percy");
        userToSave.setLastName("Jackson");
        userToSave.setBirthDate(LocalDate.of(2003, 8, 12));
        userToSave.setUsername("percy03");
        userToSave.setEmail("percy.jackson@horseland.com");
        userToSave.setPassword("password");
        userToSave.setRole(Role.STUDENT);

        User savedUser = new User(userToSave);
        savedUser.setId(UUID.randomUUID());

        // when:
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        User result = userService.addUser(userDTO);

        // then:
        assertEquals(savedUser, result);
        assertNotNull(result.getId());
        verify(userRepository, times(1)).save(any());
    }
}
