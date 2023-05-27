package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.dto.BookingShortForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Booking AS b SET b.status = ?2 WHERE b.id = ?1")
    void updateBookingStatus(Integer bookingId, Status status);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingResponseDTO(b.id, b.start, b.end, b.status, b.booker.id, b.item.id, b.item.name) FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    Collection<BookingResponseDTO> findUserBookings(Integer bookerId, PageRequest page);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingResponseDTO(b.id, b.start, b.end, b.status, b.booker.id, b.item.id, b.item.name) FROM Booking AS b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    Collection<BookingResponseDTO> findBookingsByOwner(Integer ownerId, PageRequest page);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingShortForItem(b.item.id, b.id, b.booker.id, b.start, b.end) FROM Booking AS b WHERE item IN :items AND CURRENT_TIMESTAMP < b.start AND b.status = 'APPROVED' ORDER BY b.start")
    Collection<BookingShortForItem> findNextBookings(@Param("items") Collection<Item> userItems);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingShortForItem(b.item.id, b.id, b.booker.id, b.start, b.end) FROM Booking AS b WHERE item IN :item AND CURRENT_TIMESTAMP < b.start AND b.status = 'APPROVED' ORDER BY b.start")
    Collection<BookingShortForItem> findItemNextBookings(@Param("item") Item userItem);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingShortForItem(b.item.id, b.id, b.booker.id, b.start, b.end) FROM Booking AS b WHERE item IN :items AND CURRENT_TIMESTAMP > b.start AND b.status = 'APPROVED' ORDER BY b.start DESC")
    Collection<BookingShortForItem> findLastBookings(@Param("items") Collection<Item> userItems);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingShortForItem(b.item.id, b.id, b.booker.id, b.start, b.end) FROM Booking AS b WHERE item IN :item AND CURRENT_TIMESTAMP > b.start AND b.status = 'APPROVED' ORDER BY b.start DESC")
    Collection<BookingShortForItem> findItemLastBookings(@Param("item") Item userItem);

    @Query("SELECT b FROM Booking as b WHERE b.booker.id = :id")
    Collection<Booking> findAllByBookerId(@Param("id") int bookerId);
}
