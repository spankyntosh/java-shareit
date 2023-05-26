package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient requestService;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Пришёл запрос на вещь от пользователя {} с описанием {}", userId, itemRequestDTO.getDescription());
        return requestService.createItemRequest(userId, itemRequestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришёл запрос на собственные запросы вещей от пользователя {}", userId);
        return requestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Пришёл запрос на список запросов других пользователей от пользователя {}", userId);
        return requestService.getOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("Пришёл запрос на получение запроса {} от пользователя {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}
