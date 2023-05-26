package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Запрос на получение бронирования с state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getUserItemsBookings(userId, state, from, size);

    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingRequestDTO requestDto) {
        log.info("Запрос на создание бронирования {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable @Positive long bookingId,
                                            @RequestParam(name = "approved") boolean approved) {
		log.info("Запрос на подтверждение бронирования userId={}, bookingId={}, isApproved={}", userId, bookingId, approved);
		return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
