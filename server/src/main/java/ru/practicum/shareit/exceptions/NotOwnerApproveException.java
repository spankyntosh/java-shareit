package ru.practicum.shareit.exceptions;

public class NotOwnerApproveException extends RuntimeException {
    public NotOwnerApproveException(String message) {
        super(message);
    }
}
