package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class CommentRepositoryTests {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void test1() {
        Collection<Comment> result = commentRepository.findItemComments(itemRepository.findAll());
        Assertions.assertTrue(result.size() > 0);
    }
}
