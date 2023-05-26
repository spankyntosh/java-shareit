package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Пришёл запрос на получение всех пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info(String.format("Пришёл запрос на получение пользователя с id %d", id));
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto newUser) {
        log.info("Пришёл запрос на создание пользователя");
        return userClient.createUser(newUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive long id, @RequestBody UserDto updatedUser) {
        log.info(String.format("Пришёл запрос на изменение пользователя с id %d", id));
        return userClient.updateUser(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Positive long id) {
        log.info(String.format("Пришёл запрос на удаление пользователя с id %d", id));
        userClient.deleteUser(id);
    }
}
