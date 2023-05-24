package ru.practicum.shareit.booking;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.DBBookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class BookingServiceTests {

    public static final Integer BOOKER_ID = 1;
    public static final Integer OWNER_ID = 2;
    public static final Integer NOT_OWNER_OR_USER_ID = 5;

    public static final Integer BOOKING_ID = 4;
    public static final int FROM_VALUE = 0;
    public static final int SIZE_VALUE = 20;
    public static final LocalDateTime START = LocalDateTime.now().plusMinutes(1L);
    public static final LocalDateTime END = LocalDateTime.now().plusDays(1L);
    public static final Integer ITEM_ID = 4;
    public static final String ITEM_NAME = "Вещь 1";

    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User booker;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new DBBookingService(bookingRepository, userRepository, itemRepository);
        requestDTO = new BookingRequestDTO(ITEM_ID, START, END);
        responseDTO = new BookingResponseDTO(BOOKING_ID, START, END, Status.WAITING, BOOKER_ID, ITEM_ID, ITEM_NAME);
        booker = new User(BOOKER_ID, "booker", "booker@mail.com");
        owner = new User(OWNER_ID, "owner", "owner@mail.ru");
        item = Item.builder()
                .id(ITEM_ID)
                .description("some text")
                .name(ITEM_NAME)
                .available(true)
                .owner(owner)
                .build();
        booking = new Booking(BOOKING_ID,
                START,
                END,
                item,
                booker,
                Status.WAITING);
    }

    @AfterEach
    public void afterEach() {
        item.setAvailable(true);
        item.setOwner(owner);
        requestDTO.setEnd(END);
        booking.setStatus(Status.WAITING);
    }

    @Test
    public void createBookingTest() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingResponseDTO result = bookingService.createBooking(BOOKER_ID, requestDTO);


        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getId(), responseDTO.getId()),
                () -> assertEquals(result.getStart(), responseDTO.getStart()),
                () -> assertEquals(result.getEnd(), responseDTO.getEnd()),
                () -> assertEquals(result.getBooker().getId(), responseDTO.getBooker().getId()),
                () -> assertEquals(result.getItem().getId(), responseDTO.getItem().getId()),
                () -> assertEquals(result.getItem().getName(), responseDTO.getItem().getName())
        );

    }

    @Test
    public void createBookingUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void createBookingItemNotFound() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void createBookingWrongStatus() {
        item.setAvailable(false);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void createBookingByOwner() {
        item.setOwner(booker);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(UserBookingHisOwnItemException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void createBookingEndBeforeStart() {
        requestDTO.setEnd(requestDTO.getStart().minusDays(1L));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(BookingDateException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void createBookingEndEqualsStart() {
        requestDTO.setEnd(requestDTO.getStart());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(BookingDateException.class, () -> bookingService.createBooking(BOOKER_ID, requestDTO));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    public void approveBookingApproved() {
        when(userRepository.existsById(OWNER_ID))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        doAnswer(invocationOnMock -> {
            booking.setStatus(Status.APPROVED);
            return null;
        })
                .when(bookingRepository).updateBookingStatus(anyInt(), eq(Status.APPROVED));

        BookingResponseDTO result = bookingService.approveBooking(OWNER_ID, BOOKING_ID, true);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    public void approveBookingRejected() {
        when(userRepository.existsById(OWNER_ID))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        doAnswer(invocationOnMock -> {
            booking.setStatus(Status.REJECTED);
            return null;
        })
                .when(bookingRepository).updateBookingStatus(anyInt(), eq(Status.REJECTED));

        BookingResponseDTO result = bookingService.approveBooking(OWNER_ID, BOOKING_ID, false);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    public void approvedBookingUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));

        assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(BOOKER_ID, BOOKING_ID, true));
        verify(bookingRepository, never()).updateBookingStatus(BOOKING_ID, Status.APPROVED);
    }

    @Test
    public void approvedBookingBookingNotFound() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("бронирование не найдено"));
        assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(BOOKER_ID, BOOKING_ID, true));
        verify(bookingRepository, never()).updateBookingStatus(BOOKING_ID, Status.APPROVED);
    }

    @Test
    public void approvedByNotOwner() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(NotOwnerApproveException.class, () -> bookingService.approveBooking(BOOKER_ID, BOOKING_ID, true));
        verify(bookingRepository, never()).updateBookingStatus(BOOKING_ID, Status.APPROVED);
    }

    @Test
    public void approvedBookingWrongStatus() {
        booking.setStatus(Status.APPROVED);
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(ItemNotAvailableException.class, () -> bookingService.approveBooking(OWNER_ID, BOOKING_ID, true));
        verify(bookingRepository, never()).updateBookingStatus(BOOKING_ID, Status.APPROVED);
    }

    @Test
    public void getBookingByIdTest() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        BookingResponseDTO result = bookingService.getBookingById(BOOKER_ID, BOOKING_ID);

        assertAll(
                () -> assertEquals(result.getId(), booking.getId()),
                () -> assertEquals(result.getStart(), booking.getStart()),
                () -> assertEquals(result.getEnd(), booking.getEnd()),
                () -> assertEquals(result.getStatus(), booking.getStatus())
        );
    }

    @Test
    public void getBookingUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("пользователь не найден"));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(BOOKER_ID, BOOKING_ID));
        verify(bookingRepository, never()).findById(BOOKING_ID);
    }

    @Test
    public void getBookingBookingNotFound() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenThrow(new EntityNotFoundException("бронирование не найдено"));
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(BOOKER_ID, BOOKING_ID));
    }

    @Test
    public void getBookingIllegalAccess() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(BookingIllegalAccessException.class, () -> bookingService.getBookingById(NOT_OWNER_OR_USER_ID, BOOKING_ID));
    }

    @Test
    public void getUserBookingsTest() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        PageRequest page = PageRequest.of(FROM_VALUE, SIZE_VALUE);
        when(bookingRepository.findUserBookings(BOOKER_ID, page))
                .thenReturn(List.of(responseDTO));

        String state = "CURRENT";
        responseDTO.setStart(START.minusHours(1L));
        Collection<BookingResponseDTO> result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "PAST";
        responseDTO.setStart(START.minusHours(2L));
        responseDTO.setEnd(END.minusDays(1L));
        result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "FUTURE";
        responseDTO.setStart(START.plusHours(2L));
        responseDTO.setEnd(END);
        result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "WAITING";
        responseDTO.setStart(START);
        responseDTO.setEnd(END);
        result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "REJECTED";
        responseDTO.setStatus(Status.REJECTED);
        result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "ALL";
        result = bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getUserBookingsWrongState() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        PageRequest page = PageRequest.of(FROM_VALUE, SIZE_VALUE);
        when(bookingRepository.findUserBookings(BOOKER_ID, page))
                .thenReturn(List.of(responseDTO));
        String state = "WRONG";
        assertThrows(WrongStatusException.class, () -> bookingService.getUserBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE));

    }

    @Test
    public void getUserBookingsInvalidFrom() {
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(BOOKER_ID, "CURRENT", FROM_VALUE - 10, SIZE_VALUE));
    }

    @Test
    public void getUserBookingsInvalidSize() {
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(BOOKER_ID, "CURRENT", FROM_VALUE, SIZE_VALUE - 30));
    }

    @Test
    public void getUserItemBookingsTest() {
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        PageRequest page = PageRequest.of(FROM_VALUE, SIZE_VALUE);
        when(bookingRepository.findBookingsByOwner(BOOKER_ID, page))
                .thenReturn(List.of(responseDTO));

        String state = "CURRENT";
        responseDTO.setStart(START.minusHours(1L));
        Collection<BookingResponseDTO> result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "PAST";
        responseDTO.setStart(START.minusHours(2L));
        responseDTO.setEnd(END.minusDays(1L));
        result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "FUTURE";
        responseDTO.setStart(START.plusHours(2L));
        responseDTO.setEnd(END);
        result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "WAITING";
        responseDTO.setStart(START);
        responseDTO.setEnd(END);
        result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "REJECTED";
        responseDTO.setStatus(Status.REJECTED);
        result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        state = "ALL";
        result = bookingService.getUserItemsBookings(BOOKER_ID, state, FROM_VALUE, SIZE_VALUE);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getUserItemBookingsInvalidFrom() {
        assertThrows(ValidationException.class, () -> bookingService.getUserItemsBookings(BOOKER_ID, "CURRENT", FROM_VALUE - 10, SIZE_VALUE));
    }

    @Test
    public void getUserItemBookingsInvalidSize() {
        assertThrows(ValidationException.class, () -> bookingService.getUserItemsBookings(BOOKER_ID, "CURRENT", FROM_VALUE, SIZE_VALUE - 30));
    }

}


