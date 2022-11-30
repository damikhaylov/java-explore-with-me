package ru.practicum.ewm.service.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.event.*;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.exception.BadRequestParametersException;
import ru.practicum.ewm.service.exception.ForbiddenOperationException;
import ru.practicum.ewm.service.user.User;
import ru.practicum.ewm.service.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventPrivateService extends EventService {

    private final UserRepository userRepository;

    @Autowired
    public EventPrivateService(EventRepository eventRepository, CategoryRepository categoryRepository,
                               StatsExchanger statsExchanger, UserRepository userRepository) {
        super(eventRepository, categoryRepository, statsExchanger);
        this.userRepository = userRepository;
    }

    @Transactional
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        String operationNameForLogging = "Creating a new event";
        Category category = getCategoryOrThrowException(newEventDto.getCategory(), operationNameForLogging);
        User initiator = getUserOrThrowException(userId, operationNameForLogging);
        Event event = EventMapper.toNewEvent(newEventDto, category, initiator);

        throwExceptionIfEventTimeIsBeforeXHours(event, 2, operationNameForLogging);

        Event createdEvent = eventRepository.save(event);
        log.info("{} - an event id {}=has been created", operationNameForLogging, createdEvent.getId());
        return EventMapper.toFullDto(createdEvent, 0, 0);
    }

    @Transactional
    public EventFullDto updateOwnEvent(UpdateEventDto eventDto, Long userId) {
        String operationNameForLogging = "Updating an event id=" + eventDto.getEventId();
        Event event = getEventOrThrowException(eventDto.getEventId(), operationNameForLogging);
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);
        throwExceptionForUpdatingPublishedEvent(event, operationNameForLogging);

        Optional.ofNullable(eventDto.getCategory()).ifPresent(x -> event.setCategory(
                getCategoryOrThrowException(x, operationNameForLogging)));
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        throwExceptionIfEventTimeIsBeforeXHours(event, 2, operationNameForLogging);

        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        event.setState(EventState.PENDING);

        Event updatedEvent = eventRepository.save(event);
        log.info("{} - an event id={} has been updated", operationNameForLogging, updatedEvent.getId());
        return EventMapper.toFullDto(updatedEvent, 0, 0);
    }

    @Transactional
    public EventFullDto cancelOwnEvent(Long userId, Long eventId) {
        String operationNameForLogging = "Canceling an event id=" + eventId + " by the user id=" + userId;
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionForChangingStateOfNonPendingEvent(event, operationNameForLogging);
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);
        event.setState(EventState.CANCELED);
        Event updatedEvent = eventRepository.save(event);
        log.info("{} - an event id={} has been canceled", operationNameForLogging, updatedEvent.getId());
        return EventMapper.toFullDto(updatedEvent, 0, 0);
    }

    public EventFullDto getOwnEvent(Long userId, Long eventId) {
        String operationNameForLogging = "Getting info about an event id=" + eventId + " by the user id=" + userId;
        EventWithRequestsCount eventWithRequestsCount =
                getEventWithRequestsCountOrThrowException(eventId, operationNameForLogging);
        Event event = eventWithRequestsCount.getEvent();
        throwExceptionIfUserIsNotEventInitiator(userId, event, operationNameForLogging);

        EventFullDto eventFullDto = (event.getState().equals(EventState.PUBLISHED))
                ? statsExchanger.toEventFullDtoWithStats(eventWithRequestsCount)
                : EventMapper.toFullDto(event, 0, 0);

        log.info("{} - done", operationNameForLogging);
        return eventFullDto;
    }

    public List<EventShortDto> getOwnEvents(Long userId, PageRequest pageRequest) {
        String operationNameForLogging = "Listing events created by the user id=" + userId;

        QEvent event = QEvent.event;
        BooleanBuilder filter = new BooleanBuilder();
        filter.and(event.initiator.id.eq(userId));

        Page<EventWithRequestsCount> eventsWithRequestsCount =
                eventRepository.findFilteredEventsWithRequestsCountPage(filter, false, false,
                        pageRequest);

        List<EventShortDto> eventShortDtoList =
                statsExchanger.toEventShortDtoListWithStats(eventsWithRequestsCount.toList());

        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, eventsWithRequestsCount.getTotalElements());
        return eventShortDtoList;
    }

    private User getUserOrThrowException(long userId, String operationNameForLogging) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestParametersException(
                        String.format("%s error - user parameter with id=%d does not exist",
                                operationNameForLogging, userId)));
    }

    private void throwExceptionIfUserIsNotEventInitiator(long userId, Event event, String operationNameForLogging) {
        if (userId != event.getInitiator().getId()) {
            throw new ForbiddenOperationException(String.format(
                    "%s error - the user id=%d is not the initiator of the event id=%d " +
                            "and cannot perform the operation",
                    operationNameForLogging, userId, event.getId()));
        }
    }

    private void throwExceptionForUpdatingPublishedEvent(Event event, String operationNameForLogging) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenOperationException(operationNameForLogging + " error - " +
                    "only pending or canceled events can be changed");
        }
    }
}
