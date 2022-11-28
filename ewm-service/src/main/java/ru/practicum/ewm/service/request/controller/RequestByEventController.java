package ru.practicum.ewm.service.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.ParticipationRequestDto;
import ru.practicum.ewm.service.request.service.RequestByEventPrivateService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/requests")
public class RequestByEventController {
    private final RequestByEventPrivateService requestByEventPrivateService;

    @Autowired
    public RequestByEventController(RequestByEventPrivateService requestByEventPrivateService) {
        this.requestByEventPrivateService = requestByEventPrivateService;
    }

    @PatchMapping("{reqId}/confirm")
    public ParticipationRequestDto confirmRequestForOwnEvent(@PathVariable long userId, @PathVariable long eventId,
                                                             @PathVariable long reqId) {
        return requestByEventPrivateService.confirmRequestForOwnEvent(userId, eventId, reqId);
    }

    @PatchMapping("{reqId}/reject")
    public ParticipationRequestDto cancelRequestForOwnEvent(@PathVariable long userId, @PathVariable long eventId,
                                                            @PathVariable long reqId) {
        return requestByEventPrivateService.cancelRequestForOwnEvent(userId, eventId, reqId);
    }

    @GetMapping()
    public List<ParticipationRequestDto> getRequestsForOwnEvent(@PathVariable long userId, @PathVariable long eventId) {
        return requestByEventPrivateService.getRequestsForOwnEvent(userId, eventId);
    }

}
