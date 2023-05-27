package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class CommentMapper {

    public static ResponseCommentDTO modelToResponseDTO(Comment userComment) {
        return ResponseCommentDTO.builder()
                .id(userComment.getId())
                .text(userComment.getText())
                .authorName(userComment.getAuthor().getName())
                .created(userComment.getCreated())
                .build();
    }

    public static Collection<ResponseCommentDTO> modelToResponseDTOs(Collection<Comment> items) {
        return items.stream().map(comment -> modelToResponseDTO(comment)).collect(toList());
    }
}
