package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDTO> json1;

    @Autowired
    private JacksonTester<BookingResponseDTO> json2;

    @Autowired
    private JacksonTester<BookingDto> json3;

    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;
    private BookingDto bookingDto;
    private User booker;
    private User owner;
    private Item item;
    private final static LocalDateTime START = LocalDateTime.now().plusMinutes(1L);
    private final static LocalDateTime END = LocalDateTime.now().plusDays(1L);

    @BeforeAll
    public void beforeALl() {
        booker = User.builder()
                .id(1)
                .name("booker")
                .email("booker@mail.com")
                .build();
        owner = User.builder()
                .id(2)
                .name("owner")
                .email("owner@mail.com")
                .build();
        item = Item.builder()
                .id(3)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        requestDTO = BookingRequestDTO.builder()
                .itemId(1)
                .start(START)
                .end(END)
                .build();

        responseDTO = BookingResponseDTO.builder()
                .id(1)
                .start(START)
                .end(END)
                .status(Status.APPROVED)
                .booker(new UserShort(1))
                .item(new ItemShort(1, "name"))
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(START)
                .end(END)
                .status(Status.APPROVED)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    public void requestBookingTest() throws IOException {
        JsonContent<BookingRequestDTO> result = json1.write(requestDTO);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();
    }

    @Test
    public void responseBookingTest() throws IOException {
        JsonContent<BookingResponseDTO> result = json2.write(responseDTO);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
    }

    @Test
    void bookingTest() throws IOException {
        JsonContent<BookingDto> result = json3.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@mail.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("owner");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("owner@mail.com");
    }


}
