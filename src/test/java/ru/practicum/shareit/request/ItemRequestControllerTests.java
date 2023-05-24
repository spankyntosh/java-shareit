package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestService requestService;

    LocalDateTime created = LocalDateTime.now().plusMinutes(1L);

    ItemRequestDTO requestDTO = ItemRequestDTO.builder()
            .description("some text")
            .build();

    ItemRequestResponseDTO responseDTO = ItemRequestResponseDTO.builder()
            .id(1)
            .description("some text")
            .created(created)
            .build();

    ItemRequestWithItemsResponseDTO responseWithItems = ItemRequestWithItemsResponseDTO
            .builder()
            .id(1)
            .description("some text")
            .created(created)
            .items(List.of(new ItemDto()))
            .build();

    @Test
    public void createRequest_ThenRequest_WhenResponseOk() throws Exception {
        when(requestService.createItemRequest(any(ItemRequestDTO.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDTO.getId())))
                .andExpect(jsonPath("$.description", is(responseDTO.getDescription())));

        verify(requestService).createItemRequest(any(ItemRequestDTO.class), anyInt());

    }

    @Test
    public void createRequestWithoutHeader_ThenRequest_WhenException() throws Exception {
        when(requestService.createItemRequest(any(ItemRequestDTO.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createRequestWithBlankDescription_ThenRequest_WhenException() throws Exception {
        requestDTO.setDescription("");

        when(requestService.createItemRequest(any(ItemRequestDTO.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRequestById_ThenRequest_WhenResponseOk() throws Exception {

        when(requestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(responseWithItems);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseWithItems.getId())))
                .andExpect(jsonPath("$.description", is(responseWithItems.getDescription())));

        verify(requestService).getRequestById(anyInt(), anyInt());

    }

    @Test
    public void getAllRequestsByRequester_thenRequest_whenResponseOk() throws Exception {

        when(requestService.getUserRequests(anyInt()))
                .thenReturn(List.of(responseWithItems));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestService).getUserRequests(anyInt());
    }

    @Test
    public void getOtherUsersRequests_ThenRequest_WhenResponseOk() throws Exception {
        when(requestService.getOtherUsersItemRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseWithItems));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestService).getOtherUsersItemRequests(anyInt(), anyInt(), anyInt());
    }
}
