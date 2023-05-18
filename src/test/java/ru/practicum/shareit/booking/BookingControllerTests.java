package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService bookingService;

    LocalDateTime start = LocalDateTime.now().plusMinutes(1L);
    LocalDateTime end = LocalDateTime.now().plusMinutes(2L);
    Integer from = 0;
    Integer size = 10;
    Integer itemId = 1;
    BookingRequestDTO requestDTO = BookingRequestDTO.builder()
            .itemId(itemId)
            .start(start)
            .end(end)
            .build();

    BookingResponseDTO responseDTO = BookingResponseDTO.builder()
            .id(1)
            .start(start)
            .end(end)
            .status(Status.WAITING)
            .item(new ItemShort(itemId, "Вещь 1"))
            .booker(new UserShort(1))
            .build();

    @Test
    public void createBooking_thenRequest_WhenResponseOk() throws Exception {

        when(bookingService.createBooking(anyInt(), any(BookingRequestDTO.class)))
                .thenReturn(responseDTO);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(responseDTO.getId())))
                .andExpect(jsonPath("$.start", is(responseDTO.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(responseDTO.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(responseDTO.getStatus().toString())));

    }

    @Test
    public void createBooking_thenStartDateInPast_whenValidationException() throws Exception {

        when(bookingService.createBooking(anyInt(), any(BookingRequestDTO.class)))
                .thenThrow(new ValidationException("bad request"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().is(400));
    }

    @Test
    public void approveBooking_thenIsApproved_WhenStatusApproved() throws Exception {

        responseDTO.setStatus(Status.APPROVED);

        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(responseDTO);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    public void getBooking_thenRequest_WhenStatusOk() throws Exception {

        when(bookingService.getBookingById(1, 1))
                .thenReturn(responseDTO);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    public void getUserBookings() throws Exception {

        when(bookingService.getUserBookings(anyInt(),anyString(), eq(from), eq(size)))
                .thenReturn(List.of(responseDTO));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    public void getUserItemsBookings() throws Exception {
        when(bookingService.getUserItemsBookings(anyInt(), anyString(), eq(from), eq(size)))
                .thenReturn(List.of(responseDTO));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

}
