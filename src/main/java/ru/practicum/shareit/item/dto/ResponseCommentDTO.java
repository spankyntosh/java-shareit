package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResponseCommentDTO {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
