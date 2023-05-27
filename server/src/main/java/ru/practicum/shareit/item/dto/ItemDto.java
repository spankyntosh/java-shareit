package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortForItem;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortForItem lastBooking;
    private BookingShortForItem nextBooking;
    private Collection<ResponseCommentDTO> comments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer requestId;
}
