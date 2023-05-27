package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

public class BookingMapper {
    public static Booking requestDTOToModel(BookingRequestDTO requestDTO) {
        Booking booking = new Booking();
        booking.setStart(requestDTO.getStart());
        booking.setEnd(requestDTO.getEnd());
        return booking;
    }

    public static BookingResponseDTO modelToResponseDTO(Booking booking) {
        return BookingResponseDTO
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserShort(booking.getBooker().getId()))
                .item(new ItemShort(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }
}
