package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotBelongsUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toModel;

@Service("inMemoryItemService")
public class InMemoryItemService implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        if (itemStorage.getItem(itemId).isPresent()) {
            return toItemDto(itemStorage.getItem(itemId).get());
        } else {
            throw new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId));
        }
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return itemStorage.getUserItems(userId).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return toItemDto(itemStorage.createItem(toModel(itemDto, userStorage.getUserById(userId)), userId));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto itemDto, Integer userId, Integer itemId) {
        if (!userStorage.isUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Collection<Item> userItems = itemStorage.getUserItems(userId);
        if (isNull(userItems) || userItems.stream().noneMatch(item -> item.getId().intValue() == itemId.intValue())) {
            throw new ItemNotBelongsUserException(String.format("Вещь с id %d не принадлежит пользователю с id %d", itemId, userId));
        }
        return toItemDto(itemStorage.updateItem(itemDto, userId, itemId));
    }

    @Override
    public Collection<ItemDto> searchItems(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        return itemStorage.searchItems(searchText).stream().map(ItemMapper::toItemDto).collect(toList());
    }
}
