package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Integer id, @PathVariable Integer itemId) {
        log.info("Пришёл запрос на получение вещи с id {}", itemId);
        return itemClient.getItem(id, itemId);
    }

    @PostMapping
    ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer id, @Valid @RequestBody ItemDto itemDto) {
        log.info("Пришёл запрос на создание вещи у пользователя с id {}", itemDto);
        return itemClient.createItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer id,
                                      @RequestBody UpdateItemDto updateItemDto,
                                      @PathVariable Integer itemId) {
        log.info("Пришёл запрос на обновление вещи с id {}", itemId);
        return itemClient.updateItem(id, itemId, updateItemDto);
    }

    @GetMapping
    ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer id,
                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришёл запрос на получение вещей у пользователя с id {}", id);
        return itemClient.getUserItems(id, from, size);
    }

    @GetMapping("/search")
    ResponseEntity<Object> searchItems(@RequestParam(required = true) String text,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришёл запрос по поиску вещи с описанием {}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @Valid @RequestBody RequestCommentDTO commentDTO,
                                         @PathVariable Integer itemId) {
        log.info("Пришёл запрос на добавление комментария от пользователя {}", userId);
        return itemClient.createComment(userId, itemId, commentDTO);
    }
}
