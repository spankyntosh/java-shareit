package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    @Qualifier("dbItemService")
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("dbItemService") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Integer id, @PathVariable Integer itemId) {
        log.info(String.format("Пришёл запрос на получение вещи с id %d", itemId));
        return itemService.getItem(id, itemId);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer id, @Valid @RequestBody ItemDto itemDto) {
        log.info(String.format("Пришёл запрос на создание вещи у пользователя с id %d", id));
        return itemService.createItem(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer id,
                       @RequestBody UpdateItemDto updateItemDto,
                       @PathVariable Integer itemId) {
        log.info(String.format("Пришёл запрос на обновление вещи с id %d", itemId));
        return itemService.updateItem(updateItemDto, id, itemId);
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer id,
                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        log.info(String.format("Пришёл запрос на получение вещей у пользователя с id %d", id));
        return itemService.getUserItems(id, from, size);
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestParam(required = true) String text,
                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        log.info(String.format("Пришёл запрос по поиску вещи с описанием %s", text));
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    ResponseCommentDTO createComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @Valid @RequestBody RequestCommentDTO commentDTO,
                                     @PathVariable Integer itemId) {
        log.info("Пришёл запрос на добавление комментария");
        return itemService.createComment(userId, itemId, commentDTO);
    }

}
