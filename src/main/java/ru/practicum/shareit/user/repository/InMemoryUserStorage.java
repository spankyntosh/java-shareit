package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailAlreadyInUseException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, String> emailUserIdMap = new HashMap<>();
    private static Integer userIdCounter = 1;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        if (emailUserIdMap.containsValue(user.getEmail())) {
            throw new EmailAlreadyInUseException("Данный почтовый адрес уже используется");
        }
        user.setId(userIdCounter);
        users.put(user.getId(), user);
        userIdCounter++;
        emailUserIdMap.put(user.getId(), user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (emailUserIdMap.containsValue(user.getEmail())) {
            if (!emailUserIdMap.get(user.getId()).contentEquals(user.getEmail())) {
                throw new EmailAlreadyInUseException("Данный почтовый адрес уже используется");
            }
        }
        users.put(user.getId(), user);
        emailUserIdMap.put(user.getId(), user.getEmail());
        System.out.println(users);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        users.remove(userId);
        emailUserIdMap.remove(userId);
    }

    @Override
    public boolean isUserExists(Integer userId) {
        return users.containsKey(userId);
    }
}
