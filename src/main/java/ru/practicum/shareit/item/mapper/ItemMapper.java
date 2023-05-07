package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto()
                .toBuilder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .id(item.getId())
                .build();
    }

    public static Item toModel(ItemDto itemDto, User user) {
        return new Item()
                .toBuilder()
                .owner(user)
                .name(itemDto.getName())
                .available(isNull(itemDto.getAvailable()) || itemDto.getAvailable())
                .description(itemDto.getDescription())
                .build();
    }

    public static Collection<ItemDto> toItemDtos(Collection<Item> items) {
        return items.stream().map(item -> toItemDto(item)).collect(toList());
    }
}
