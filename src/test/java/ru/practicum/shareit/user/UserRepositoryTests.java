package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class UserRepositoryTests {

    @Autowired
    UserRepository userRepository;

    @Test
    public void findUsers() {
        Collection<User> result = userRepository.findAll();
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void existByEmailTest() {
        boolean result = userRepository.existsByEmail("user_1@mail.com");
        Assertions.assertTrue(result);
    }
}
