package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.handlers.CustomExceptionsHandler;

public class ErrorHandlerTests {

    private CustomExceptionsHandler handler = new CustomExceptionsHandler();

    @Test
    public void entityNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("пользователь не найден");
        ErrorResponse response = handler.handleEntityNotFoundException(exception);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(exception.getMessage(), response.getMessage());
    }
}
