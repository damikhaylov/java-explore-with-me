package ru.practicum.ewm.service.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.EventState;
import ru.practicum.ewm.service.exception.BadRequestParametersException;
import ru.practicum.ewm.service.exception.ForbiddenOperationException;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;
import ru.practicum.ewm.service.request.*;
import ru.practicum.ewm.service.user.User;
import ru.practicum.ewm.service.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RequestByRequesterPrivateService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestByRequesterPrivateService(RequestRepository requestRepository,
                                            EventRepository eventRepository,
                                            UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        String operationNameForLogging =
                String.format("Creating a new request for event id=%d from user id=%d", eventId, userId);
        User requester = getUserOrThrowException(userId, operationNameForLogging);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);

        throwExceptionIfRequesterIsEventInitiator(userId, event, operationNameForLogging);
        throwExceptionForNonPublishedEvent(event, operationNameForLogging);
        throwExceptionIfRequesterAlreadyExists(requester, event, operationNameForLogging);
        throwExceptionForParticipantLimit(event, operationNameForLogging);

        Request request = new Request(null, event, requester, LocalDateTime.now(),
                (event.getRequestModeration()) ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        Request createdRequest = requestRepository.save(request);
        log.info("{} - a request id {}=has been created", operationNameForLogging, createdRequest.getId());
        return RequestMapper.toRequestDto(createdRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelOwnRequest(long userId, long requestId) {
        String operationNameForLogging = "Canceling an own request id=" + requestId + " by the user id=" + userId;
        Request request = getRequestOrThrowException(requestId, operationNameForLogging);
        throwExceptionIfUserIsNotRequester(userId, request, operationNameForLogging);
        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        log.info("{} - an request id={} has been canceled", operationNameForLogging, updatedRequest.getId());
        return RequestMapper.toRequestDto(updatedRequest);
    }

    public List<ParticipationRequestDto> getOwnRequests(long userId) {
        String operationNameForLogging = "Listing requests created by the user id=" + userId;
        User requester = getUserOrThrowException(userId, operationNameForLogging);
        List<Request> requests = requestRepository.findByRequester(requester);
        log.info("{} - a list of {} items has been compiled",
                operationNameForLogging, requests.size());
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    private Request getRequestOrThrowException(long requestId, String operationNameForLogging) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - request with id=%d was not found",
                                operationNameForLogging, requestId)));
    }

    private Event getEventOrThrowException(long eventId, String operationNameForLogging) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - event with id=%d was not found",
                                operationNameForLogging, eventId)));
    }

    private User getUserOrThrowException(long userId, String operationNameForLogging) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestParametersException(
                        String.format("%s error - user parameter with id=%d does not exist",
                                operationNameForLogging, userId)));
    }

    private void throwExceptionIfRequesterIsEventInitiator(long userId, Event event, String operationNameForLogging) {
        if (userId == event.getInitiator().getId()) {
            throw new ForbiddenOperationException(String.format(
                    "%s error - the user id=%d is the initiator of the event id=%d " +
                            "and cannot send a request to participate in it",
                    operationNameForLogging, userId, event.getId()));
        }
    }

    private void throwExceptionForNonPublishedEvent(Event event, String operationNameForLogging) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenOperationException(String.format("%s error - " +
                            "event id=%d has not been published and it is impossible to participate in it",
                    operationNameForLogging, event.getId()));
        }
    }

    private void throwExceptionIfRequesterAlreadyExists(User requester, Event event, String operationNameForLogging) {
        if (requestRepository.existsByRequesterAndEvent(requester, event)) {
            throw new ForbiddenOperationException(String.format(
                    "%s error - the request from user id=%d for event id=%d already exists",
                    operationNameForLogging, requester.getId(), event.getId()));
        }
    }

    private void throwExceptionForParticipantLimit(Event event, String operationNameForLogging) {
        int participantsCount = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() <= participantsCount) {
            throw new ForbiddenOperationException(String.format("%s error - " +
                            "event id=%d has reached the limit of participants",
                    operationNameForLogging, event.getId()));
        }
    }

    private void throwExceptionIfUserIsNotRequester(long userId, Request request, String operationNameForLogging) {
        if (userId != request.getRequester().getId()) {
            throw new ForbiddenOperationException(String.format(
                    "%s error - the user id=%d is not the owner of the request id=%d " +
                            "and cannot perform the operation",
                    operationNameForLogging, userId, request.getId()));
        }
    }

}
