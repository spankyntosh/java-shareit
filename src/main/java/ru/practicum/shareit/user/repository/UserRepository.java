package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    Collection<User> getUsers();

    User getUserById(Integer userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);

    boolean isUserExists(Integer userId);

}
