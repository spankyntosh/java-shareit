package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.dto.BookingShortForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class BookingRepositoryTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAllByOwnerTest() {
        PageRequest page = PageRequest.of(0, 10);
        Collection<BookingResponseDTO> result = bookingRepository.findBookingsByOwner(1, page);
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void findAllByBookerIdTest() {
        Collection<Booking> result =  bookingRepository.findAllByBookerId(2);
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void findByIdTest() {
      Booking booking = bookingRepository.findById(1).get();
      Assertions.assertEquals(1, booking.getId());
      Assertions.assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    public void findUserBookingsTest() {
        PageRequest page = PageRequest.of(0, 10);
        Collection<BookingResponseDTO> result = bookingRepository.findUserBookings(2, page);
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void findNextBookingsTest() {
        Collection<BookingShortForItem> result = bookingRepository.findNextBookings(itemRepository.findAll());
        Assertions.assertTrue(result.size() == 0);
    }

    @Test
    public void findItemNextBookingsTest() {
        Collection<BookingShortForItem> result = bookingRepository.findItemNextBookings(itemRepository.findById(1).get());
        Assertions.assertTrue(result.size() == 0);
    }

    @Test
    public void findLastBookingsTest() {
        Collection<BookingShortForItem> result = bookingRepository.findLastBookings(itemRepository.findAll());
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void findItemLastBookingsTest() {
        Collection<BookingShortForItem> result = bookingRepository.findItemLastBookings(itemRepository.findById(1).get());
        Assertions.assertTrue(result.size() > 0);
    }
}
