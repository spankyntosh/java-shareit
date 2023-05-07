package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotBelongsUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service("dbItemService")
public class DBItemService implements ItemService{

    ItemRepository itemRepository;
    UserRepository userRepository;

    @Autowired
    public DBItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId))));
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return toItemDtos(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return toItemDto(itemRepository.save(toModel(itemDto, user)));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto dto, Integer userId, Integer itemId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", userId)));
        if (item.getOwner().getId().intValue() != userId.intValue()) {
            throw new ItemNotBelongsUserException(String.format("Вещь с id %d не принадлежит пользователю с id %d", itemId, userId));
        }
        if (nonNull(dto.getName())) {
            item.setName(dto.getName());
        }
        if (nonNull(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (nonNull(dto.getAvailable())) {
            item.setAvailable(dto.getAvailable());
        }
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public Collection<ItemDto> searchItems(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<ItemDto>();
        }
        return toItemDtos(itemRepository.searchItems(searchText));
    }
}
