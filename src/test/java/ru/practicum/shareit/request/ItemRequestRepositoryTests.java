package ru.practicum.shareit.request;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class ItemRequestRepositoryTests {

    @Autowired
    ItemRequestRepository requestRepository;

    @Test
    public void test1() {
        PageRequest page = PageRequest.of(0, 10);
        Collection<ItemRequest> result = requestRepository.findAllExceptRequester(1, page);
        Assertions.assertEquals(1, result.size());
    }
}
