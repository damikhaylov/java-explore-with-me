package ru.practicum.ewm.service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.EventState;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.service.EventAdminService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @Autowired
    public EventAdminController(EventAdminService eventAdminService) {
        this.eventAdminService = eventAdminService;
    }

    @PutMapping("/{eventId}")
    public EventFullDto putEvent(@PathVariable long eventId, @RequestBody AdminUpdateEventRequest eventDto) {
        return eventAdminService.putEvent(eventDto, eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto cancelEvent(@PathVariable long eventId) {
        return eventAdminService.cancelEvent(eventId);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        return eventAdminService.publishEvent(eventId);
    }

    @GetMapping()
    public List<EventFullDto> getEvents(@RequestParam Optional<Long[]> users,
                                        @RequestParam Optional<EventState[]> states,
                                        @RequestParam Optional<Long[]> categories,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        Optional<LocalDateTime> rangeStart,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        Optional<LocalDateTime> rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        return eventAdminService.getEvents(users, states, categories, rangeStart, rangeEnd,
                new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
