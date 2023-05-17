package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestResponseDTO createItemRequest(ItemRequestDTO requestDTO, Integer userId);

    Collection<ItemRequestWithItemsResponseDTO> getUserRequests(Integer userId);

    Collection<ItemRequestWithItemsResponseDTO> getOtherUsersItemRequests(Integer userId, Integer from, Integer size);

    ItemRequestWithItemsResponseDTO getRequestById(Integer userId, Integer requestId);
}
