package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {
    @NotNull
    private int itemId;
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    @NotNull(message = "необходимо указать время начала бронирования")
    private LocalDateTime start;
    @Future(message = "время окончания бронирования не может быть в прошлом")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @NotNull(message = "необходимо указать время окончания бронирования")
    private LocalDateTime end;

    @Override
    public String toString() {
        return "BookingRequest{" +
                "itemId=" + itemId +
                '}';
    }
}
