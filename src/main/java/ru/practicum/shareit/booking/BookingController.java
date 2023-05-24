package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDTO createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @Valid @RequestBody BookingRequestDTO requestDTO) {
        log.info("Пришёл запрос на создание бронирования от пользователя {}", userId);
        return bookingService.createBooking(userId, requestDTO);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDTO approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId,
                                             @RequestParam(name = "approved") boolean isApproved) {
        log.info("Пришёл запрос на подтверждение бронирования {} от пользователя {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable Integer bookingId) {
        log.info("Пришёл запрос на получение бронирования {} от пользователя {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponseDTO> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                          @RequestParam(required = false, defaultValue = "0") int from,
                                                          @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришёл запрос на получение всех бронирований пользователя {}", userId);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDTO> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                               @RequestParam(required = false, defaultValue = "0") int from,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Пришёл запрос на получение бронирований вещей пользователя {}", userId);
        return bookingService.getUserItemsBookings(userId, state, from, size);

    }

}
