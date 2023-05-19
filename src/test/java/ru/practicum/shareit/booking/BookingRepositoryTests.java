package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.Collection;

@DataJpaTest
@Sql(value = "/testSchema.sql")
public class BookingRepositoryTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void findAllByOwner() {
        PageRequest page = PageRequest.of(0, 10);
        Collection<BookingResponseDTO> result = bookingRepository.findBookingsByOwner(1, page);
        Assertions.assertTrue(result.size() > 0);
    }
}
