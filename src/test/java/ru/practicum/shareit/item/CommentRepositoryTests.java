package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
    public void findItemsCommentsTest() {
        Collection<Comment> result = commentRepository.findItemComments(itemRepository.findAll());
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void findItemCommentsTest() {
        Item item = Item.builder()
                .id(1)
                .description("Description")
                .available(true)
                .build();
        Collection<Comment> result = commentRepository.findItemComments(item);
        Assertions.assertTrue(result.size() > 0);
    }

}
