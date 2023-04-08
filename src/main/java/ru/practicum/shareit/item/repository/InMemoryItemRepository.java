package ru.practicum.shareit.item.repository;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private static int itemIdCounter = 1;
    private final Map<Integer, List<Item>> items = new HashMap<>();

    @Override
    public Optional<Item> getItem(Integer itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public Collection<Item> getUserItems(Integer userId) {
        return items.get(userId);
    }

    @Override
    public Item createItem(Item item, Integer userId) {
        item.setId(itemIdCounter);
        if (!items.containsKey(userId)) {
            items.put(userId, new LinkedList<>());
        }
        items.get(userId).add(item);
        itemIdCounter++;
        return item;
    }

    @Override
    public Item updateItem(UpdateItemDto item, Integer userId, Integer itemId) {
        List<Item> userItems = items.get(userId);
        Item updatedItem = userItems.stream().filter(listItem -> listItem.getId() == itemId).findFirst().get();
        if (nonNull(item.getName())) {
            updatedItem.setName(item.getName());
        }
        if (nonNull(item.getDescription())) {
            updatedItem.setDescription(item.getDescription());
        }
        if (nonNull(item.getAvailable())) {
            updatedItem.setAvailable(item.getAvailable());
        }
        userItems.removeIf(itemInList -> Objects.equals(itemInList.getId(), itemId));

        userItems.add(updatedItem);
        return updatedItem;
    }

    @Override
    public Collection<Item> searchItems(String searchText) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getDescription().toLowerCase()
                .contains(searchText.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(toList());
    }
}
