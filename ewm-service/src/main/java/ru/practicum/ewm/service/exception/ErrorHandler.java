package ru.practicum.ewm.service.exception;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleIdWasNotFoundException(final IdWasNotFoundException exception) {
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
    public ResponseEntity<?> handleThrowableArgumentException(final MethodArgumentTypeMismatchException exception) {
        return getResponseByThrowable(exception,
                "Errors in request parameters",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowableArgumentException(final ConstraintViolationException exception) {
        return getResponseByThrowable(exception,
                "Errors in request parameters",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowableArgumentException(final MissingServletRequestParameterException exception) {
        return getResponseByThrowable(exception,
                "Errors in request parameters",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleThrowableArgumentException(final BadRequestParametersException exception) {
        return getResponseByThrowable(exception,
                "Errors in request parameters",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleForbiddenOperationException(final ForbiddenOperationException exception) {
        return getResponseByThrowable(exception,
                "For the requested operation the conditions are not met",
                HttpStatus.FORBIDDEN);
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

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        List<String> errors = Collections.singletonList(stackTrace);

        return new ResponseEntity<>(new ErrorResponse(throwable.getMessage(), reason, status, errors), status);
    }
}
