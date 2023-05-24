package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItem(Integer userId, Integer itemId);

    Collection<ItemDto> getUserItems(Integer userId, Integer from, Integer size);

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(UpdateItemDto dto, Integer userId, Integer itemId);

    Collection<ItemDto> searchItems(String searchText, Integer from, Integer size);

    ResponseCommentDTO createComment(Integer userId, Integer itemId, RequestCommentDTO requestCommentDTO);
}
