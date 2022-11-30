package ru.practicum.ewm.service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.UpdateEventDto;
import ru.practicum.ewm.service.event.service.EventPrivateService;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @Autowired
    public EventPrivateController(EventPrivateService eventPrivateService) {
        this.eventPrivateService = eventPrivateService;
    }

    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Validated NewEventDto newEventDto) {
        return eventPrivateService.createEvent(newEventDto, userId);
    }

    @PatchMapping
    public EventFullDto updateOwnEvent(@PathVariable Long userId,
                                       @RequestBody @Validated UpdateEventDto updateEventDto) {
        return eventPrivateService.updateOwnEvent(updateEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelOwnEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventPrivateService.cancelOwnEvent(userId, eventId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOwnEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventPrivateService.getOwnEvent(userId, eventId);
    }

    @GetMapping()
    public List<EventShortDto> getOwnEvents(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size) {
        return eventPrivateService.getOwnEvents(userId, new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
