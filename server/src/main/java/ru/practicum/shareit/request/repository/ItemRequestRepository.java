package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("SELECT r FROM ItemRequest AS r WHERE r.requestor.id = ?1 ORDER BY r.created")
    Collection<ItemRequest> findByRequesterId(Integer requesterId);

    @Query("SELECT r FROM ItemRequest AS r WHERE r.requestor.id not in :requesterId ORDER BY r.created")
    Collection<ItemRequest> findAllExceptRequester(@Param("requesterId") Integer requesterId, PageRequest page);
}
