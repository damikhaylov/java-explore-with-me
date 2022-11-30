package ru.practicum.ewm.service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.repository.SortState;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.service.EventPublicService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path = "/events")
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @Autowired
    public EventPublicController(EventPublicService eventPublicService) {
        this.eventPublicService = eventPublicService;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        return eventPublicService.getEvent(eventId, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping()
    public List<EventShortDto> getEvents(@RequestParam Optional<String> text,
                                         @RequestParam Optional<Long[]> categories,
                                         @RequestParam Optional<Boolean> paid,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         Optional<LocalDateTime> rangeStart,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         Optional<LocalDateTime> rangeEnd,
                                         @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam Optional<SortState> sort,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        return eventPublicService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                new CustomPageRequest(from, size, Sort.unsorted()), request.getRequestURI(), request.getRemoteAddr());
    }
}
