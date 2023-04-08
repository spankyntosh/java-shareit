package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer id, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer id,
                       @RequestBody UpdateItemDto updateItemDto,
                       @PathVariable Integer itemId) {
        return itemService.updateItem(updateItemDto, id, itemId);
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer id) {
        return itemService.getUserItems(id);
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestParam(required = true) String text) {
        return itemService.searchItems(text);
    }

}
