package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static java.util.Objects.nonNull;
import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
public class InMemoryUserService implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public InMemoryUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> getUsers() {
        return toUserDtos(userRepository.getUsers());
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto user) {
        return toUserDto(userRepository.createUser(DtoToModel(user, null)));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto user) {
        if (!userRepository.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        User userFromStorage = userRepository.getUserById(userId);
        User intermediateUser = new User(userFromStorage.getId(), userFromStorage.getName(), userFromStorage.getEmail());
        if (nonNull(user.getEmail())) {
            intermediateUser.setEmail(user.getEmail());
        }
        if (nonNull(user.getName())) {
            intermediateUser.setName(user.getName());
        }
        return toUserDto(userRepository.updateUser(intermediateUser));
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с таким id %d не найден", userId));
        }
        userRepository.deleteUser(userId);
    }
}
