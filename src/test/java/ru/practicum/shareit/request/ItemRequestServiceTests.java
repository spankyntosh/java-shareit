package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseDTO;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTests {

    private UserRepository userRepository;
    private ItemRequestRepository requestRepository;
    private ItemRequestService requestService;
    private ItemRequest itemRequest;
    private ItemRequestDTO requestDTO;
    private User requester;
    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "user";
    private static final String USER_EMAIL = "user@mail.com";
    private static final Integer REQUEST_ID = 2;
    private static final String REQUEST_DESCRIPTION = "request description";
    private static final LocalDateTime CREATED = LocalDateTime.now().minusMinutes(1L);
    private static final Integer FROM = 0;
    private static final Integer SIZE = 10;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        requestService = new ItemRequestServiceImpl(userRepository, requestRepository);
        requester = new User(USER_ID, USER_NAME, USER_EMAIL);
        requestDTO = new ItemRequestDTO(REQUEST_DESCRIPTION);
        itemRequest = new ItemRequest(REQUEST_ID, REQUEST_DESCRIPTION, requester, CREATED, new HashSet<Item>());
    }

    @Test
    public void createItemRequest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(requester));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestResponseDTO result = requestService.createItemRequest(requestDTO, USER_ID);
        assertAll(
                () -> assertEquals(itemRequest.getId(), result.getId()),
                () -> assertEquals(itemRequest.getDescription(), result.getDescription()),
                () -> assertEquals(itemRequest.getCreated(), result.getCreated())
        );
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void createItemRequestUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> requestService.createItemRequest(requestDTO, USER_ID));
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void getRequestById() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(requestRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestWithItemsResponseDTO result = requestService.getRequestById(USER_ID, REQUEST_ID);
        assertAll(
                () -> assertEquals(itemRequest.getId(), result.getId()),
                () -> assertEquals(itemRequest.getDescription(), result.getDescription()),
                () -> assertEquals(itemRequest.getCreated(), result.getCreated())
        );
        verify(requestRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getRequestByIdUserNotFound() {
        when(userRepository.existsById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestById(USER_ID, REQUEST_ID));
        verify(requestRepository, never()).findById(anyInt());
    }

    @Test
    public void GetRequestByIdRequestNotFound() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(requestRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("запрос не найден"));
        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestById(USER_ID, REQUEST_ID));
    }

    @Test
    public void getUserRequests() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(requester));
        when(requestRepository.findByRequesterId(anyInt()))
                .thenReturn(List.of(itemRequest));
        Collection<ItemRequestWithItemsResponseDTO> result = requestService.getUserRequests(USER_ID);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(requestRepository, times(1)).findByRequesterId(anyInt());
    }

    @Test
    public void getUserRequestsUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> requestService.getUserRequests(USER_ID));
        verify(requestRepository, never()).findByRequesterId(anyInt());
    }

    @Test
    public void getOtherUsersRequests() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        PageRequest page = PageRequest.of(FROM, SIZE);
        when(requestRepository.findAllExceptRequester(USER_ID, page))
                .thenReturn(List.of(itemRequest));
        Collection<ItemRequestWithItemsResponseDTO> result = requestService.getOtherUsersItemRequests(USER_ID, FROM, SIZE);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(requestRepository, times(1)).findAllExceptRequester(anyInt(), eq(page));
    }

    @Test
    public void getOtherUsersRequestsUserNotFound() {
        PageRequest page = PageRequest.of(FROM, SIZE);
        when(userRepository.existsById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> requestService.getOtherUsersItemRequests(USER_ID, FROM, SIZE));
        verify(requestRepository, never()).findAllExceptRequester(anyInt(), eq(page));
    }

    @Test
    public void getOtherUsersRequestsInvalidFrom() {
        PageRequest page = PageRequest.of(FROM, SIZE);
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        assertThrows(ValidationException.class, () -> requestService.getOtherUsersItemRequests(USER_ID, FROM - 10, SIZE));
        verify(requestRepository, never()).findAllExceptRequester(anyInt(), eq(page));
    }

    @Test
    public void getOtherUsersRequestsInvalidSize() {
        PageRequest page = PageRequest.of(FROM, SIZE);
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        assertThrows(ValidationException.class, () -> requestService.getOtherUsersItemRequests(USER_ID, FROM, SIZE - 10));
        verify(requestRepository, never()).findAllExceptRequester(anyInt(), eq(page));
    }
}
