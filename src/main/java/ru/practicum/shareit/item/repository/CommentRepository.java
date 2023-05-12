package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment AS c WHERE item in ?1")
    Collection<Comment> findItemComments(Item item);

    @Query("SELECT c FROM Comment AS c WHERE item IN :items")
    Collection<Comment> findItemComments(@Param("items") Collection<Item> userItems);
}
