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
        log.info("Пришёл запрос на создание бронирования");
        return bookingService.createBooking(userId, requestDTO);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDTO approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable Integer bookingId,
                                     @RequestParam(name = "approved") boolean isApproved) {
        log.info("Пришёл запрос на подтверждение бронирования");
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer bookingId) {
        log.info("Пришёл запрос на получение бронирования");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponseDTO> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Пришёл запрос на получение всех бронирований пользователя");
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDTO> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Пришёл запрос на получение бронирований вещей пользователя");
        return bookingService.getUserItemsBookings(userId, state);

    }

}
