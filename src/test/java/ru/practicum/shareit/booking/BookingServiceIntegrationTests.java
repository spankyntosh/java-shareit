package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/testSchema.sql")
@SpringBootTest
public class BookingServiceIntegrationTests {

    private final BookingService bookingService;

    @Test
    public void addBooking() {
        BookingRequestDTO requestDTO = BookingRequestDTO.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusMinutes(2L))
                .end(LocalDateTime.now().plusDays(1L))
                .build();
        BookingResponseDTO result = bookingService.createBooking(2, requestDTO);
        assertNotNull(result);
        assertEquals(1, result.getItem().getId());
    }

    @Test
    public void approveBooking() {
        BookingResponseDTO result = bookingService.approveBooking(1, 1, true);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    public void getBookingById() {
        BookingResponseDTO result = bookingService.getBookingById(1, 1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void getUserBookings() {
        String state = "FUTURE";
        Collection<BookingResponseDTO> bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);

        state = "PAST";
        bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);

        state = "REJECTED";
        bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);

        state = "WAITING";
        bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);

        state = "ALL";
        bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);

        state = "CURRENT";
        bookingDtos = bookingService.getUserBookings(1, state, 0, 10);
        assertEquals(List.of(), bookingDtos);
    }

    @Test
    public void getOtherUsersBookings() {
        String state = "FUTURE";
        Collection<BookingResponseDTO> bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);

        state = "PAST";
        bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);

        state = "REJECTED";
        bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);

        state = "WAITING";
        bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);

        state = "ALL";
        bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);

        state = "CURRENT";
        bookingDtos = bookingService.getUserItemsBookings(1, state, 0, 10);
        assertTrue(bookingDtos.size() > 0);
    }
}
