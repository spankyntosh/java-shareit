package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotBelongsUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCommentDTO;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.DBItemService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ItemServiceTests {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository requestRepository;
    private ItemService itemService;
    private ItemDto createItemDTO;
    private static final Integer ITEM_ID = 1;
    private static final String ITEM_NAME = "вещь 1";
    private static final String ITEM_DESCRIPTION = "some text";
    private ItemDto createItemDTOWithRequest;
    private static final Integer ITEM_ID_WITH_REQUEST = 2;
    private static final String ITEM_NAME_WITH_REQUEST = "вещь 2";
    private static final String ITEM_DESCRIPTION_WITH_REQUEST = "another text";
    private static final Integer ITEM_REQUEST_ID = 3;
    private User user;
    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "user";
    private static final String USER_EMAIL = "user@mail.com";
    private Item item;
    private Item itemWithRequest;
    private ItemRequest itemRequest;
    private UpdateItemDto updateItemDto;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        itemService = new DBItemService(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);
        createItemDTO = ItemDto.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .build();
        createItemDTOWithRequest = ItemDto.builder()
                .id(ITEM_ID_WITH_REQUEST)
                .name(ITEM_NAME_WITH_REQUEST)
                .description(ITEM_DESCRIPTION_WITH_REQUEST)
                .requestId(ITEM_REQUEST_ID)
                .build();
        user = new User(USER_ID, USER_NAME, USER_EMAIL);
        item = Item.builder()
                .id(ITEM_ID)
                .available(true)
                .description(ITEM_DESCRIPTION)
                .name(ITEM_NAME)
                .owner(user)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("some request info")
                .created(LocalDateTime.now())
                .build();
        itemWithRequest = Item.builder()
                .id(ITEM_ID_WITH_REQUEST)
                .available(true)
                .description(ITEM_DESCRIPTION_WITH_REQUEST)
                .name(ITEM_NAME_WITH_REQUEST)
                .owner(user)
                .request(itemRequest)
                .build();
        updateItemDto = UpdateItemDto.builder()
                .name("new name")
                .description("new description")
                .available(true)
                .build();
    }

    @Test
    public void createItem() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto result = itemService.createItem(createItemDTO, USER_ID);
        assertAll(
                () -> assertEquals(item.getId(), result.getId()),
                () -> assertEquals(item.getName(), result.getName()),
                () -> assertEquals(item.getDescription(), result.getDescription()),
                () -> assertEquals(item.getAvailable(), result.getAvailable())
        );
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void createItemWithRequest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemWithRequest);
        ItemDto result = itemService.createItem(createItemDTOWithRequest, USER_ID);
        assertAll(
                () -> assertEquals(itemWithRequest.getId(), result.getId()),
                () -> assertEquals(itemWithRequest.getName(), result.getName()),
                () -> assertEquals(itemWithRequest.getDescription(), result.getDescription()),
                () -> assertEquals(itemWithRequest.getAvailable(), result.getAvailable()),
                () -> assertEquals(itemWithRequest.getRequest().getId(), result.getRequestId())
        );
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void createItemUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));
        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(createItemDTO, USER_ID));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void createItemRequestNotFound() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("запрос не найден"));
        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(createItemDTOWithRequest, USER_ID));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void getItemById() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto result = itemService.getItem(USER_ID, ITEM_ID);
        assertAll(
                () -> assertEquals(item.getId(), result.getId()),
                () -> assertEquals(item.getName(), result.getName()),
                () -> assertEquals(item.getDescription(), result.getDescription()),
                () -> assertEquals(item.getAvailable(), result.getAvailable())
        );
        verify(itemRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getItemByIdItemNotFound() {
        when(itemRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("предмет не найден"));
        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(USER_ID, ITEM_ID));
    }

    @Test
    public void updateItem() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto result = itemService.updateItem(updateItemDto, USER_ID, ITEM_ID);
        assertAll(
                () -> assertEquals(item.getId(), result.getId()),
                () -> assertEquals(item.getName(), result.getName()),
                () -> assertEquals(item.getDescription(), result.getDescription()),
                () -> assertEquals(item.getAvailable(), result.getAvailable())
        );
    }

    @Test
    public void updateItemWrongUser() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(ItemNotBelongsUserException.class, () -> itemService.updateItem(updateItemDto, 2, ITEM_ID));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void searchItems() {
        when(itemRepository.searchItems(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item));
        Collection<ItemDto> result = itemService.searchItems("search text", 0, 10);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void searchItemsBlankText() {
        when(itemRepository.searchItems(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item));
        Collection<ItemDto> result = itemService.searchItems("", 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void createComment() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByBookerId(anyInt()))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(new Comment());

    }
}
