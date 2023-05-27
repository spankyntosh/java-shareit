package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;

import java.util.Collection;

public interface BookingService {
    BookingResponseDTO createBooking(Integer userId, BookingRequestDTO requestDTO);

    BookingResponseDTO approveBooking(Integer userId, Integer bookingId, Boolean isApproved);

    BookingResponseDTO getBookingById(Integer userId, Integer bookingId);

    Collection<BookingResponseDTO> getUserBookings(Integer userId, String state, Integer from, Integer size);

    Collection<BookingResponseDTO> getUserItemsBookings(Integer userId, String state, Integer from, Integer size);
}
