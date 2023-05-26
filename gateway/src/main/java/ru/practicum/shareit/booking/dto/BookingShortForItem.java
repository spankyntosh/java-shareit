package ru.practicum.shareit.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingShortForItem {

    private int itemId;
    private int id;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingShortForItem(int itemId, int bookingId, int bookerId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.id = bookingId;
        this.bookerId = bookerId;
        this.start = start;
        this.end = end;
    }
}
