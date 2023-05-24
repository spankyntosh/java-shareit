package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Integer id, @PathVariable Integer itemId) {
        log.info("Пришёл запрос на получение вещи с id {}", itemId);
        return itemService.getItem(id, itemId);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer id, @Valid @RequestBody ItemDto itemDto) {
        log.info("Пришёл запрос на создание вещи у пользователя с id {}", itemDto);
        return itemService.createItem(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer id,
                       @RequestBody UpdateItemDto updateItemDto,
                       @PathVariable Integer itemId) {
        log.info("Пришёл запрос на обновление вещи с id {}", itemId);
        return itemService.updateItem(updateItemDto, id, itemId);
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer id,
                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришёл запрос на получение вещей у пользователя с id {}", id);
        return itemService.getUserItems(id, from, size);
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestParam(required = true) String text,
                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Пришёл запрос по поиску вещи с описанием {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    ResponseCommentDTO createComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @Valid @RequestBody RequestCommentDTO commentDTO,
                                     @PathVariable Integer itemId) {
        log.info("Пришёл запрос на добавление комментария от пользователя {}", userId);
        return itemService.createComment(userId, itemId, commentDTO);
    }

}
