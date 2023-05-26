package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestResponseDTO createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Пришёл запрос на вещь от пользователя {} с описанием {}", userId, itemRequestDTO.getDescription());
        return requestService.createItemRequest(itemRequestDTO, userId);
    }

    @GetMapping
    public Collection<ItemRequestWithItemsResponseDTO> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл запрос на собственные запросы вещей от пользователя {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithItemsResponseDTO> getOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Пришёл запрос на список запросов других пользователей от пользователя {}", userId);
        return requestService.getOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsResponseDTO getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                          @PathVariable Integer requestId) {
        log.info("Пришёл запрос на получение запроса {} от пользователя {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}
