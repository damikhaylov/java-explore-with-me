package ru.practicum.ewm.service.exception;

public class IdWasNotFoundException extends RuntimeException {

    public IdWasNotFoundException(String message) {
        super(message);
    }
}