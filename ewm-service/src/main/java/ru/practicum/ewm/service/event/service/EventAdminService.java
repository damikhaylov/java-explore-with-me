package ru.practicum.ewm.service.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.event.*;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventAdminService extends EventService {

    public EventAdminService(EventRepository eventRepository, CategoryRepository categoryRepository,
                             StatsExchanger statsExchanger) {
        super(eventRepository, categoryRepository, statsExchanger);
    }

    @Transactional
    public EventFullDto putEvent(AdminUpdateEventRequest eventDto, Long eventId) {
        String operationNameForLogging = "Updating an event id=" + eventId + " by admin";
        EventWithRequestsCount eventWithRequestsCount =
                getEventWithRequestsCountOrThrowException(eventId, operationNameForLogging);
        Event event = eventWithRequestsCount.getEvent();

        Optional.ofNullable(eventDto.getCategory()).ifPresent(x -> event.setCategory(
                getCategoryOrThrowException(x, operationNameForLogging)));
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getLocation()).ifPresent(
                x -> {
                    event.setLatitude(x.getLat());
                    event.setLongitude(x.getLon());
                });

        Event updatedEvent = eventRepository.save(event);

        EventFullDto eventFullDto =
                statsExchanger.toEventFullDtoWithStats(updatedEvent, eventWithRequestsCount.getRequestCount());

        log.info("{} - an event id={} has been updated", operationNameForLogging, updatedEvent.getId());
        return eventFullDto;
    }

    @Transactional
    public EventFullDto cancelEvent(Long eventId) {
        String operationNameForLogging = "Canceling an event id=" + eventId + " by admin";
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionForChangingStateOfNonPendingEvent(event, operationNameForLogging);

        event.setState(EventState.CANCELED);

        Event updatedEvent = eventRepository.save(event);
        log.info("{} - an event id={} has been canceled", operationNameForLogging, updatedEvent.getId());
        return EventMapper.toFullDto(updatedEvent, 0, 0);
    }

    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        String operationNameForLogging = "Publishing an event id=" + eventId + " by admin";
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        throwExceptionForChangingStateOfNonPendingEvent(event, operationNameForLogging);
        throwExceptionIfEventTimeIsBeforeXHours(event, 1, operationNameForLogging);

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        Event updatedEvent = eventRepository.save(event);
        log.info("{} - an event id={} has been published", operationNameForLogging, updatedEvent.getId());
        return EventMapper.toFullDto(updatedEvent, 0, 0);
    }

    public List<EventFullDto> getEvents(Optional<Long[]> users, Optional<EventState[]> states, Optional<Long[]> categories,
                                        Optional<LocalDateTime> rangeStart, Optional<LocalDateTime> rangeEnd,
                                        PageRequest pageRequest) {
        String operationNameForLogging = "Listing filtering events for admin";
        QEvent event = QEvent.event;

        BooleanBuilder filter = new BooleanBuilder();
        categories.ifPresent(x -> filter.and(event.category.id.in(Arrays.asList(x))));
        states.ifPresent(x -> filter.and(event.state.in(Arrays.asList(x))));
        users.ifPresent(x -> filter.and(event.initiator.id.in(Arrays.asList(x))));
        rangeStart.ifPresent(x -> filter.and(event.eventDate.goe(x)));
        rangeEnd.ifPresent(x -> filter.and(event.eventDate.loe(x)));

        Page<EventWithRequestsCount> eventsWithRequestsCount =
                eventRepository.findFilteredEventsWithRequestsCountPage(filter, false, false,
                        pageRequest);

        List<EventFullDto> eventFullDtoList =
                statsExchanger.toEventFullDtoListWithStats(eventsWithRequestsCount.toList());

        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, eventsWithRequestsCount.getTotalElements());
        return eventFullDtoList;
    }

}
