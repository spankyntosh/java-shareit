package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class ItemRequestMapper {

    public static ItemRequestResponseDTO modelToItemRequestResponseDTO(ItemRequest itemRequest) {
        return ItemRequestResponseDTO
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestWithItemsResponseDTO modelToItemRequestWithItemsDTO(ItemRequest itemRequest) {
        return ItemRequestWithItemsResponseDTO
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemDtos(itemRequest.getItems()))
                .build();
    }


    public static Collection<ItemRequestWithItemsResponseDTO> modelToItemRequestResponseDTOs(Collection<ItemRequest> itemRequests) {

        return itemRequests.stream()
                .map(ItemRequestMapper::modelToItemRequestWithItemsDTO)
                .collect(toList());
    }
}
