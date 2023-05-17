package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestWithItemsResponseDTO {

    private int id;
    private String description;
    private LocalDateTime created;
    Collection<ItemDto> items;
}
