package ru.practicum.ewm.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleNonExistentIdException(final NonExistentIdException exception) {
        return getResponseByThrowable(exception,
                "The required object was not found",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowableArgumentException(final MethodArgumentNotValidException exception) {
        return getResponseByThrowable(exception,
                "Errors in request parameters",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowableIntegrityViolation(final DataIntegrityViolationException exception) {
        return getResponseByThrowable(exception,
                "Integrity constraint has been violated",
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowable(final Throwable throwable) {
        return getResponseByThrowable(throwable, "Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> getResponseByThrowable(final Throwable throwable,
                                                     String reason,
                                                     final HttpStatus status) {
        log.warn("{} - {}", status, throwable.getMessage());
        log.debug("Throwable stack trace:", throwable);
        return new ResponseEntity<>(
                new ErrorResponse(throwable.getMessage(), reason, status),
                status);
    }
}
