package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/testSchema.sql")
@SpringBootTest
public class ItemServiceIntegrationTests {

    private final ItemService itemService;

    @Test
    public void getItems() {
        Collection<ItemDto> result = itemService.getUserItems(2, 0, 10);
        assertEquals(2, result.size());
    }

    @Test
    public void getItemById() {
        ItemDto result = itemService.getItem(1, 1);
        assertEquals(1, result.getId());
    }

    @Test
    public void searchByText() {
        Collection<ItemDto> result = itemService.searchItems("Description", 0, 10);
        assertEquals(4, result.size());
    }
}
