package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnknownStatusResponse {
    private String error;

    public UnknownStatusResponse(String error) {
        this.error = error;
    }
}
