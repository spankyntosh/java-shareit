package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Collection<Item> findAllByOwnerId(Integer ownerId, PageRequest pageRequest);

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%'))" +
    " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available=true")
    Collection<Item> searchItems(String searchText, PageRequest pageRequest);
}
