package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItem(Integer itemId);
    Collection<ItemDto> getUserItems(Integer userId);
    ItemDto createItem(ItemDto itemDto, Integer userId);
    ItemDto updateItem(UpdateItemDto dto, Integer userId, Integer itemId);
    Collection<ItemDto> searchItems(String searchText);
}
