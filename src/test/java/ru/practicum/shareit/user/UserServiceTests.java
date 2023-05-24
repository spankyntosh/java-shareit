package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EmailAlreadyInUseException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.DBUserService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    private static final Integer USER_ID = 1;
    private static final Integer ANOTHER_USER_ID = 3;
    private static final String USER_NAME = "user";

    private static final String ANOTHER_USER_NAME = "another user";
    private static final String USER_EMAIL = "user@mail.com";
    private static final String ANOTHER_USER_EMAIL = "anotherUser@mail.com";
    private UserService userService;
    private UserRepository userRepository;
    private User user;
    private User anotherUser;
    private UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new DBUserService(userRepository);
        user = new User(USER_ID, USER_NAME, USER_EMAIL);
        anotherUser = new User(ANOTHER_USER_ID, ANOTHER_USER_NAME, ANOTHER_USER_EMAIL);
        userDto = new UserDto(USER_ID, USER_NAME, USER_EMAIL);
    }

    @Test
    public void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto result = userService.createUser(userDto);
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getName(), result.getName()),
                () -> assertEquals(user.getEmail(), result.getEmail())
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void getUserById() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        UserDto result = userService.getUserById(USER_ID);
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getName(), result.getName()),
                () -> assertEquals(user.getEmail(), result.getEmail())
        );
        verify(userRepository).findById(anyInt());
    }

    @Test
    public void getUserByIdNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(anyInt()));
    }

    @Test
    public void updateUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto result = userService.updateUser(USER_ID, userDto);
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getName(), result.getName()),
                () -> assertEquals(user.getEmail(), result.getEmail())
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void userUpdateUserEmailExists() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(true);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto result = userService.updateUser(USER_ID, userDto);
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getName(), result.getName()),
                () -> assertEquals(user.getEmail(), result.getEmail())
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void userUpdateUserEmailExistsAnotherUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.existsByEmail(anyString()))
                .thenReturn(true);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.updateUser(USER_ID, userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void deleteUserById() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        userService.deleteUser(USER_ID);
        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    public void deleteUserByIdUserNotExists() {
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(USER_ID));
        verify(userRepository, never()).deleteById(USER_ID);
    }
}
