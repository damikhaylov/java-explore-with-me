package ru.practicum.ewm.service.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.StatsExchanger;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.EventState;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.exception.BadRequestParametersException;
import ru.practicum.ewm.service.exception.ForbiddenOperationException;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;

import java.time.LocalDateTime;

@Slf4j
public abstract class EventService {
    protected final EventRepository eventRepository;
    protected final CategoryRepository categoryRepository;
    protected final StatsExchanger statsExchanger;

    @Autowired
    public EventService(EventRepository eventRepository,
                        CategoryRepository categoryRepository,
                        StatsExchanger statsExchanger) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.statsExchanger = statsExchanger;
    }

    protected Event getEventOrThrowException(long eventId, String operationNameForLogging) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - event with id=%d was not found",
                                operationNameForLogging, eventId)));
    }

    protected EventWithRequestsCount getEventWithRequestsCountOrThrowException(long eventId,
                                                                               String operationNameForLogging) {
        return eventRepository.findEventWithRequestsCountById(eventId).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - event with id=%d was not found",
                                operationNameForLogging, eventId)));
    }

    protected Category getCategoryOrThrowException(long categoryId, String operationNameForLogging) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new BadRequestParametersException(
                        String.format("%s error - category parameter with id=%d does not exist",
                                operationNameForLogging, categoryId)));
    }

    protected void throwExceptionForChangingStateOfNonPendingEvent(Event event, String operationNameForLogging) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ForbiddenOperationException(operationNameForLogging + " error - " +
                    "operation is allowed only for pending events");
        }
    }

    protected void throwExceptionIfEventTimeIsBeforeXHours(Event event, int hoursLimit,
                                                           String operationNameForLogging) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(hoursLimit))) {
            throw new ForbiddenOperationException(String.format("%s error - " +
                    "the time of event will come earlier than in %d hours", operationNameForLogging, hoursLimit));
        }
    }

}
