package ru.practicum.ewm.service.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.BadRequestParametersException;
import ru.practicum.ewm.service.exception.ForbiddenOperationException;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;
import ru.practicum.ewm.service.request.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class RequestByEventPrivateService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RequestByEventPrivateService(RequestRepository requestRepository,
                                        EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public ParticipationRequestDto confirmRequestForOwnEvent(long userId, long eventId, long requestId) {
        String operationNameForLogging = String.format(
                "Confirming a request id=%d for event id=%d owned by user id=%d", requestId, eventId, userId);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);

        throwExceptionForUnlimitedEvent(event, operationNameForLogging);
        throwExceptionForUnmoderatedEvent(event, operationNameForLogging);
        long participantReserve = getParticipantReserveOrThrowException(event, operationNameForLogging);

        Request request = getRequestOrThrowException(requestId, operationNameForLogging);
        throwExceptionIfRequestIsNotForEvent(eventId, request, operationNameForLogging);
        request.setStatus(RequestStatus.CONFIRMED);
        Request updatedRequest = requestRepository.save(request);
        log.info("{} - an request id={} from user id={} has been canceled by user id={}",
                operationNameForLogging, updatedRequest.getId(), updatedRequest.getRequester().getId(), userId);

        participantReserve--;
        if (participantReserve == 0) {
            requestRepository.rejectAllPendingRequestsForEvent(event.getId());
        }

        return RequestMapper.toRequestDto(updatedRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelRequestForOwnEvent(long userId, long eventId, long requestId) {
        String operationNameForLogging = String.format(
                "Canceling a request id=%d for event id=%d owned by user id=%d", requestId, eventId, userId);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);
        Request request = getRequestOrThrowException(requestId, operationNameForLogging);
        throwExceptionIfRequestIsNotForEvent(eventId, request, operationNameForLogging);
        request.setStatus(RequestStatus.REJECTED);
        Request updatedRequest = requestRepository.save(request);
        log.info("{} - an request id={} from user id={} has been canceled by user id={}",
                operationNameForLogging, updatedRequest.getId(), updatedRequest.getRequester().getId(), userId);
        return RequestMapper.toRequestDto(updatedRequest);
    }

    public List<ParticipationRequestDto> getRequestsForOwnEvent(long userId, long eventId) {
        String operationNameForLogging =
                String.format("Listing requests for event id=%d by the user id=%d", eventId, userId);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);
        List<Request> requests = requestRepository.findByEvent(event);
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

    private void throwExceptionForUnlimitedEvent(Event event, String operationNameForLogging) {
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ForbiddenOperationException(String.format("%s error - event id=%d is unlimited for participants",
                    operationNameForLogging, event.getId()));
        }
    }

    private void throwExceptionForUnmoderatedEvent(Event event, String operationNameForLogging) {
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ForbiddenOperationException(String.format("%s error - event id=%d is unmoderated",
                    operationNameForLogging, event.getId()));
        }
    }

    private long getParticipantReserveOrThrowException(Event event, String operationNameForLogging) {
        long participantsCount = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() <= participantsCount) {
            throw new ForbiddenOperationException(String.format("%s error - " +
                            "event id=%d has reached the limit of participants",
                    operationNameForLogging, event.getId()));
        }
        return event.getParticipantLimit() - participantsCount;
    }

    private void throwExceptionIfUserIsNotEventInitiator(long userId, Event event, String operationNameForLogging) {
        if (userId != event.getInitiator().getId()) {
            throw new ForbiddenOperationException(String.format(
                    "%s error - the user id=%d is not the initiator of the event id=%d " +
                            "and cannot perform the operation",
                    operationNameForLogging, userId, event.getId()));
        }
    }

    private void throwExceptionIfRequestIsNotForEvent(long eventId, Request request, String operationNameForLogging) {
        if (eventId != request.getEvent().getId()) {
            throw new BadRequestParametersException(String.format(
                    "%s error - the request id=%d does not relate to the event id=%d, " +
                            "operation cannot be performed",
                    operationNameForLogging, eventId, request.getId()));
        }
    }

}
