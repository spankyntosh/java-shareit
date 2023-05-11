package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingResponseDTO {
    private final int id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Status status;
    private final UserShort booker;
    private final ItemShort item;

    public BookingResponseDTO(int id, LocalDateTime start, LocalDateTime end, Status status,
                              Integer userId, Integer itemId, String itemName) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = new UserShort(userId);
        this.item = new ItemShort(itemId, itemName);
    }
}
