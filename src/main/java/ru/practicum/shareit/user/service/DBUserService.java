package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyInUseException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static java.util.Objects.nonNull;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
@Service("dbUserService")
public class DBUserService implements UserService{

    UserRepository userRepository;

    @Autowired
    public DBUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> getUsers() {
        return toUserDtos(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId))));
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            //throw new EmailAlreadyInUseException("Данный почтовый адрес уже используется");
        }
        return toUserDto(userRepository.save(dtoToModel(user, null)));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto user) {
        User userFromRepository = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        if (userRepository.existsByEmail(user.getEmail())) {
            if (!userFromRepository.getEmail().contentEquals(user.getEmail())) {
                throw new EmailAlreadyInUseException("Данный почтовый адрес уже используется");
            }
        }
        User intermediateUser = new User(userFromRepository.getId(), userFromRepository.getName(), userFromRepository.getEmail());

        if (nonNull(user.getEmail())) {
            intermediateUser.setEmail(user.getEmail());
        }
        if (nonNull(user.getName())) {
            intermediateUser.setName(user.getName());
        }
        return toUserDto(userRepository.save(intermediateUser));
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        userRepository.deleteById(userId);
    }
}
