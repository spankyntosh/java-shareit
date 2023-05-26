package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.modelToItemRequestResponseDTOs;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.modelToItemRequestWithItemsDTO;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }


    @Override
    public ItemRequestResponseDTO createItemRequest(ItemRequestDTO requestDTO, Integer userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        ItemRequest newRequest = new ItemRequest();
        newRequest.setRequestor(requester);
        newRequest.setDescription(requestDTO.getDescription());
        newRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.modelToItemRequestResponseDTO(requestRepository.save(newRequest));
    }

    @Override
    public Collection<ItemRequestWithItemsResponseDTO> getUserRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));

        return modelToItemRequestResponseDTOs(requestRepository.findByRequesterId(userId));
    }

    @Override
    public Collection<ItemRequestWithItemsResponseDTO> getOtherUsersItemRequests(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return modelToItemRequestResponseDTOs(requestRepository.findAllExceptRequester(userId, pageRequest));
    }

    @Override
    public ItemRequestWithItemsResponseDTO getRequestById(Integer userId, Integer requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }

        return modelToItemRequestWithItemsDTO(requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с id %d не найден", userId))));
    }
}
