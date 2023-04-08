package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotBelongsUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toModel;

@Service
public class InMemoryItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public InMemoryItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        if (itemRepository.getItem(itemId).isPresent()) {
            return toItemDto(itemRepository.getItem(itemId).get());
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId));
        }
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId) {
        if (!userRepository.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return itemRepository.getUserItems(userId).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        if (!userRepository.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return toItemDto(itemRepository.createItem(toModel(itemDto, userRepository.getUserById(userId)), userId));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto itemDto, Integer userId, Integer itemId) {
        if (!userRepository.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Collection<Item> userItems = itemRepository.getUserItems(userId);
        if (isNull(userItems) || userItems.stream().noneMatch(item -> item.getId() == itemId)) {
            throw new ItemNotBelongsUserException(String.format("Вещь с id %d не принадлежит пользователю с id %d", itemId, userId));
        }
        return toItemDto(itemRepository.updateItem(itemDto, userId, itemId));
    }

    @Override
    public Collection<ItemDto> searchItems(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.searchItems(searchText).stream().map(ItemMapper::toItemDto).collect(toList());
    }
}
