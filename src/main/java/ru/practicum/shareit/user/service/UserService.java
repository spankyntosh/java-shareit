package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getUsers();
    UserDto getUserById(Integer userId);
    UserDto createUser(UserDto user);
    UserDto updateUser(Integer userId, UserDto user);
    void deleteUser(Integer userId);
}
