package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItem(Integer itemId);
    Collection<Item> getUserItems(Integer userId);
    Item createItem(Item item, Integer userId);
    Item updateItem(UpdateItemDto item, Integer userId, Integer itemId);
    Collection<Item> searchItems(String searchText);

}
