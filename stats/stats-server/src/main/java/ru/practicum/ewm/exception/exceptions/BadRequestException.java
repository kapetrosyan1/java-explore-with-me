package ru.practicum.ewm.exception.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String m) {
        super(m);
    }
}
