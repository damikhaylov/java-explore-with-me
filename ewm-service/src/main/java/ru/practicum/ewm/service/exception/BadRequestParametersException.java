package ru.practicum.ewm.service.exception;

public class BadRequestParametersException extends RuntimeException {

    public BadRequestParametersException(String message) {
        super(message);
    }
}