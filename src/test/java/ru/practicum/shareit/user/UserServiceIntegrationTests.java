package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/testSchema.sql")
@SpringBootTest
public class UserServiceIntegrationTests {

    private final UserService userService;

    @Test
    public void createUser() {
        User newUser = User.builder().name("user").email("user@email.com").build();
        UserDto response = userService.createUser(UserMapper.toUserDto(newUser));
        assertNotNull(response.getId());
        assertEquals(newUser.getName(), response.getName());
        assertEquals(newUser.getEmail(), response.getEmail());
    }

    @Test
    public void getUsers() {
        Collection<UserDto> users = userService.getUsers();
        assertEquals(3, users.size());
    }

    @Test
    public void getUserById() {
        UserDto user = userService.getUserById(1);
        assertEquals("user_1", user.getName());
    }

    @Test
    public void updateUser() {
        UserDto updatedUser = new UserDto(1, "new name", "user_1@mail.com");
        UserDto result = userService.updateUser(1, updatedUser);
        assertEquals("new name", result.getName());
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1);
        assertEquals(2, userService.getUsers().size());
    }

}
