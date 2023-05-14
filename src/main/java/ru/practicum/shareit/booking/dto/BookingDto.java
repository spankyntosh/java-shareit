package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Integer id;
    @NotNull(groups = BookingController.class)
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private DateTimeFormat start;
    @NotNull(groups = BookingController.class)
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private DateTimeFormat end;
    @NotNull(groups = BookingController.class)
    private User booker;
    @NotNull(groups = BookingController.class)
    private Item item;
    @NotNull(groups = BookingController.class)
    private Status status;
}
