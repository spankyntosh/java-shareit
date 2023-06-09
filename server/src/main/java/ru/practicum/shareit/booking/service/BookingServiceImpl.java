package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.dto.BookingMapper.modelToResponseDTO;
import static ru.practicum.shareit.booking.dto.BookingMapper.requestDTOToModel;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingResponseDTO createBooking(Integer userId, BookingRequestDTO requestDTO) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item bookingItem = itemRepository.findById(requestDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", requestDTO.getItemId())));
        if (!bookingItem.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Предмет с id %d не доступен для бронирования", requestDTO.getItemId()));
        }
        if (bookingItem.getOwner().getId().equals(userId)) {
            throw new UserBookingHisOwnItemException("Попытка забронировать свой предмет");
        }
        if (requestDTO.getEnd().isBefore(requestDTO.getStart()) || requestDTO.getEnd().isEqual(requestDTO.getStart())) {
            throw new BookingDateException("Время начала бронирования не может совпадать или быть позже окончания бронирования");
        }
        Booking newBooking = requestDTOToModel(requestDTO);
        newBooking.setStart(requestDTO.getStart());
        newBooking.setEnd(requestDTO.getEnd());
        newBooking.setStatus(Status.WAITING);
        newBooking.setBooker(booker);
        newBooking.setItem(bookingItem);

        return modelToResponseDTO(bookingRepository.save(newBooking));
    }

    @Override
    public BookingResponseDTO approveBooking(Integer userId, Integer bookingId, Boolean isApproved) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Booking approvingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        if (!(approvingBooking.getItem().getOwner().getId().equals(userId))) {
            throw new NotOwnerApproveException("подтверждение бронирования должно производиться владельцем");
        }
        if (approvingBooking.getStatus() != Status.WAITING) {
            throw new ItemNotAvailableException("Предмет недоступен или бронирование уже подтверждено или отклонено");
        }
        if (isApproved) {
            bookingRepository.updateBookingStatus(bookingId, Status.APPROVED);
        } else {
            bookingRepository.updateBookingStatus(bookingId, Status.REJECTED);
        }
        return modelToResponseDTO(bookingRepository.findById(bookingId).get());
    }

    @Override
    public BookingResponseDTO getBookingById(Integer userId, Integer bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        Booking requestedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        boolean isUserOwner = requestedBooking.getItem().getOwner().getId().equals(userId);
        boolean isUserBooker = requestedBooking.getBooker().getId().intValue() == userId.intValue();
        if (!(isUserOwner | isUserBooker)) {
            throw new BookingIllegalAccessException("Данное бронирование недоступно");
        }
        return modelToResponseDTO(requestedBooking);
    }

    @Override
    public Collection<BookingResponseDTO> getUserBookings(Integer userId, String state, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        State bookingState = State.parse(state);
        LocalDateTime now = LocalDateTime.now();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Collection<BookingResponseDTO> userBookings = bookingRepository.findUserBookings(userId, page);

        switch (bookingState) {
            case CURRENT:
                return userBookings
                        .stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(toList());
            case PAST:
                return userBookings
                        .stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(toList());
            case FUTURE:
                return userBookings
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(toList());
            case WAITING:
                return userBookings
                        .stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .collect(toList());
            case REJECTED:
                return userBookings
                        .stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED)
                        .collect(toList());
            default:
                return userBookings;
        }
    }

    @Override
    public Collection<BookingResponseDTO> getUserItemsBookings(Integer userId, String state, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        State bookingState = State.parse(state);
        LocalDateTime now = LocalDateTime.now();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Collection<BookingResponseDTO> userItems = bookingRepository.findBookingsByOwner(userId, page);
        switch (bookingState) {
            case CURRENT:
                return userItems
                        .stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(toList());
            case PAST:
                return userItems
                        .stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(toList());
            case FUTURE:
                return userItems
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(toList());
            case WAITING:
                return userItems
                        .stream()
                        .filter(booking -> booking.getStatus() == Status.WAITING)
                        .collect(toList());
            case REJECTED:
                return userItems
                        .stream()
                        .filter(booking -> booking.getStatus() == Status.REJECTED)
                        .collect(toList());
            default:
                return userItems;
        }


    }

}
