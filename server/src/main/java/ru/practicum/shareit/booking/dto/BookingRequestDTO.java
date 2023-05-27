package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingRequestDTO {

    private int itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    @Override
    public String toString() {
        return "BookingRequest{" +
                "itemId=" + itemId +
                '}';
    }
}
