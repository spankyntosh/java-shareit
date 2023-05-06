package ru.practicum.shareit.exceptions;

public class ItemNotBelongsUserException extends RuntimeException {
    public ItemNotBelongsUserException(String message) {
        super(message);
    }
}
