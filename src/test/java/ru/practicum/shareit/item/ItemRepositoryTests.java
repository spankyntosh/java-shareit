package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class ItemRepositoryTests {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void findAllByOwner() {
        PageRequest page = PageRequest.of(0, 10);
        Collection<Item> result = itemRepository.findAllByOwnerId(1, page);
        Assertions.assertTrue(result.size() > 0);
    }
}
