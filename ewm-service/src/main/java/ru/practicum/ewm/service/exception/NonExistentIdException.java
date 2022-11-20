package ru.practicum.ewm.service.exception;

public class NonExistentIdException extends RuntimeException {

    public NonExistentIdException(String message) {
        super(message);
    }
}