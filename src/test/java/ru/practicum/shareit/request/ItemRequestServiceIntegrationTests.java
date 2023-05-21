package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/testSchema.sql")
@SpringBootTest
public class ItemRequestServiceIntegrationTests {

    private final ItemRequestService requestService;

    @Test
    public void createRequest() {
        ItemRequestDTO requestDTO = ItemRequestDTO.builder()
                .description("some description")
                .build();

        ItemRequestResponseDTO result = requestService.createItemRequest(requestDTO, 1);
        assertEquals(requestDTO.getDescription(), result.getDescription());
    }

    @Test
    public void getItemRequestById() {
        ItemRequestWithItemsResponseDTO result = requestService.getRequestById(1, 1);
        assertEquals("description", result.getDescription());
    }

    @Test
    public void getItemRequestsByOwner() {
        Collection<ItemRequestWithItemsResponseDTO> result = requestService.getUserRequests(1);
        assertEquals(1, result.size());
    }

    @Test
    public void getOtherUsersItemRequests() {
        Collection<ItemRequestWithItemsResponseDTO> result = requestService.getOtherUsersItemRequests(1, 0, 10);
        assertEquals(1, result.size());
    }

}
