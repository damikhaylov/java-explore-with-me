package ru.practicum.ewm.service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final List<String> errors;

    public ErrorResponse(String message, String reason, HttpStatus status, List<String> errors) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
