package ru.practicum.shareit.exceptions;

public class UserBookingHisOwnItemException extends RuntimeException {
    public UserBookingHisOwnItemException(String message) {
        super(message);
    }
}
