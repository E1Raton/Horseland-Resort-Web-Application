package com.software_design.horseland.service;

import com.software_design.horseland.exception.DatabaseValidationException;
import com.software_design.horseland.model.Role;
import com.software_design.horseland.model.User;
import com.software_design.horseland.model.UserDTO;
import com.software_design.horseland.repository.UserRepository;
import com.software_design.horseland.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordUtil passwordUtil;

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
        userToSave.setPassword(passwordUtil.hashPassword("password"));
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

    @Test
    void testUpdateUser() throws DatabaseValidationException {
        // given:
        UUID uuid = UUID.randomUUID();
        User user = new User();
        user.setId(uuid);
        user.setFirstName("Percy");
        user.setLastName("Jackson");
        user.setBirthDate(LocalDate.of(2003, 8, 12));
        user.setUsername("percy03");
        user.setEmail("percy.jackson@horseland.com");
        user.setPassword(passwordUtil.hashPassword("password"));
        user.setRole(Role.STUDENT);

        User updatedUser = new User();
        updatedUser.setId(uuid);
        updatedUser.setFirstName("Ela");
        updatedUser.setLastName("Smith");
        updatedUser.setBirthDate(LocalDate.of(2005, 7, 21));
        updatedUser.setUsername("ela_smith");
        updatedUser.setEmail("ela@example.com");
        updatedUser.setPassword(passwordUtil.hashPassword("password123"));
        updatedUser.setRole(Role.INSTRUCTOR);

        UserDTO updatedUserDTO =
                new UserDTO("Ela", "Smith", LocalDate.of(2005, 7, 21), "ela_smith", "ela@example.com", "password123", Role.INSTRUCTOR);

        // when:
        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);
        User result = userService.updateUser(uuid, updatedUserDTO);

        // then:
        assertEquals("Ela", result.getFirstName());
        verify(userRepository, times(1)).findById(uuid);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateUserNotFound() {
        // given:
        UUID uuid = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();

        // when:
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        // then:
        assertThrows(DatabaseValidationException.class, () -> userService.updateUser(uuid, userDTO));
        verify(userRepository, times(1)).findById(uuid);
    }

    @Test
    void testDeleteUser() {
        // given:
        UUID uuid = UUID.randomUUID();

        // when:
        doNothing().when(userRepository).deleteById(uuid);
        userService.deleteUser(uuid);

        // then:
        verify(userRepository, times(1)).deleteById(uuid);
    }
}
