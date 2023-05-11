package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingShortForItem {

    private int itemId;
    private int bookingId;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingShortForItem(int itemId, int bookingId, int bookerId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.bookingId = bookingId;
        this.bookerId = bookerId;
        this.start = start;
        this.end = end;
    }
}
