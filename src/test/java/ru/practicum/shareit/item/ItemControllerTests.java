package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    ItemDto requestDTO = ItemDto.builder()
            .name("name1")
            .description("some text")
            .available(true)
            .requestId(1)
            .build();

    ItemDto responseDTO = ItemDto.builder()
            .id(1)
            .name("name1")
            .description("some text")
            .available(true)
            .requestId(1)
            .build();

    UpdateItemDto updateItemDto = UpdateItemDto
            .builder()
            .name("new name")
            .build();

    RequestCommentDTO commentDTO = RequestCommentDTO
            .builder()
            .text("some text")
            .build();

    @Test
    public void createItem_ThenRequest_WhenResponseOk() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(requestDTO))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDTO.getId())))
                .andExpect(jsonPath("$.name", is(responseDTO.getName())))
                .andExpect(jsonPath("$.description", is(responseDTO.getDescription())))
                .andExpect(jsonPath("$.requestId", is(responseDTO.getRequestId())));

        verify(itemService).createItem(any(ItemDto.class), anyInt());
    }

    @Test
    public void createItemWithoutHeader_ThenRequest_WhenException() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createItemBlankName_ThenRequest_WhenException() throws Exception {
        requestDTO.setName("");
        when(itemService.createItem(any(ItemDto.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createItemBlankDescription_ThenRequest_WhenException() throws Exception {
        requestDTO.setDescription("");
        requestDTO.setName("name1");
        when(itemService.createItem(any(ItemDto.class), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getItem_ThenRequest_WhenResponseOk() throws Exception {
        requestDTO.setDescription("some text");

        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).getItem(anyInt(), anyInt());
    }

    @Test
    public void getItemInvalidId_ThenRequest_WhenException() throws Exception {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenThrow(new EntityNotFoundException("Пук пук"));

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateItem_ThenRequest_WhenResponseOk() throws Exception {
        responseDTO.setName("new name");

        when(itemService.updateItem(any(UpdateItemDto.class), anyInt(), anyInt()))
                .thenReturn(responseDTO);

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(responseDTO.getName())));

        verify(itemService).updateItem(any(UpdateItemDto.class), anyInt(), anyInt());
    }

    @Test
    public void getAllItemsByOwner_ThenRequest_WhenResponseOk() throws Exception {

        when(itemService.getUserItems(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDTO));

        mvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemByText_thenRequest_whenResponseOk() throws Exception {
        String searchRequest = "searchRequest";
        when(itemService.searchItems(eq(searchRequest), eq(0), eq(10)))
                .thenReturn(List.of(responseDTO));

        mvc.perform(get("/items/search")
                        .param("text", searchRequest)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk());

        verify(itemService).searchItems(eq(searchRequest), eq(0), eq(10));
    }

    @Test
    void createComment_thenRequest_whenResponseOk() throws Exception {
        mvc.perform(post("/items/{id}/comment", 1)
                        .content(mapper.writeValueAsString(commentDTO))
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).createComment(anyInt(), anyInt(), eq(commentDTO));
    }
}
