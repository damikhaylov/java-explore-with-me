package ru.practicum.ewm.service.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.ParticipationRequestDto;
import ru.practicum.ewm.service.request.service.RequestByRequesterPrivateService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class RequestByRequesterController {
    private final RequestByRequesterPrivateService requestByRequesterPrivateService;

    @Autowired
    public RequestByRequesterController(RequestByRequesterPrivateService requestByRequesterPrivateService) {
        this.requestByRequesterPrivateService = requestByRequesterPrivateService;
    }

    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable long userId, @RequestParam long eventId) {
        return requestByRequesterPrivateService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelOwnRequest(@PathVariable long userId, @PathVariable long requestId) {
        return requestByRequesterPrivateService.cancelOwnRequest(userId, requestId);
    }

    @GetMapping()
    public List<ParticipationRequestDto> getOwnRequests(@PathVariable long userId) {
        return requestByRequesterPrivateService.getOwnRequests(userId);
    }
}
