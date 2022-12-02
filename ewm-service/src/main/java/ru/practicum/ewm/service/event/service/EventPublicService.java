package ru.practicum.ewm.service.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.event.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.SortState;
import ru.practicum.ewm.service.exception.ForbiddenOperationException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventPublicService extends EventService {

    public EventPublicService(EventRepository eventRepository, CategoryRepository categoryRepository,
                              StatsExchanger statsExchanger) {
        super(eventRepository, categoryRepository, statsExchanger);
    }

    public EventFullDto getEvent(Long eventId, String uri, String ip) {
        String operationNameForLogging = "Getting info about an event id=" + eventId + " by public";
        EventWithRequestsCount eventWithRequestsCount =
                getEventWithRequestsCountOrThrowException(eventId, operationNameForLogging);
        Event event = eventWithRequestsCount.getEvent();
        throwExceptionForGettingNonPublishedEventForPublic(event, operationNameForLogging);

        EventFullDto eventFullDto = statsExchanger.toEventFullDtoWithStats(eventWithRequestsCount);
        statsExchanger.saveStat(uri, ip);

        log.info("{} - done", operationNameForLogging);
        return eventFullDto;
    }

    public List<EventShortDto> getEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid,
                                         Optional<LocalDateTime> rangeStart, Optional<LocalDateTime> rangeEnd,
                                         boolean onlyAvailable, Optional<SortState> sort,
                                         PageRequest pageRequest, String uri, String ip) {

        String operationNameForLogging = "Listing filtering events for public";
        QEvent event = QEvent.event;

        BooleanBuilder filter = new BooleanBuilder();
        filter.and(event.state.eq(EventState.PUBLISHED));
        text.ifPresent(x -> filter.andAnyOf(event.annotation.containsIgnoreCase(x),
                event.description.containsIgnoreCase(x)));
        categories.ifPresent(x -> filter.and(event.category.id.in(Arrays.asList(x))));
        paid.ifPresent(x -> filter.and(event.paid.eq(x)));
        if (rangeStart.isEmpty() || rangeEnd.isEmpty()) {
            filter.and(event.eventDate.after(LocalDateTime.now()));
        } else {
            filter.and(event.eventDate.between(rangeStart.get(), rangeEnd.get()));
        }

        List<EventShortDto> eventShortDtoList;
        final long competeListSize;
        if (sort.isPresent() && sort.get().equals(SortState.VIEWS)) {
            List<EventWithRequestsCount> eventsWithRequestsCount =
                    eventRepository.findFilteredEventsWithRequestsCountList(filter, onlyAvailable, false);
            eventShortDtoList = statsExchanger.toEventShortDtoListWithStats(eventsWithRequestsCount);
            eventShortDtoList.sort(Comparator
                    .comparing(EventShortDto::getViews, Comparator.reverseOrder()).thenComparing(EventShortDto::getId));
            competeListSize = eventShortDtoList.size();
            eventShortDtoList = getPageSizedSublist(eventShortDtoList, (int) pageRequest.getOffset(),
                    pageRequest.getPageSize());
        } else {
            boolean sortedByDate = sort.isPresent() && sort.get().equals(SortState.EVENT_DATE);
            Page<EventWithRequestsCount> eventsWithRequestsCount =
                    eventRepository.findFilteredEventsWithRequestsCountPage(filter, onlyAvailable, sortedByDate,
                            pageRequest);
            competeListSize = eventsWithRequestsCount.getTotalElements();
            eventShortDtoList =
                    statsExchanger.toEventShortDtoListWithStats(eventsWithRequestsCount.toList());
        }

        statsExchanger.saveStat(uri, ip);
        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, competeListSize);
        return eventShortDtoList;
    }

    private void throwExceptionForGettingNonPublishedEventForPublic(Event event, String operationNameForLogging) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenOperationException(operationNameForLogging + " error - " +
                    "only published events can be viewed by public");
        }
    }

    private <T> List<T> getPageSizedSublist(List<T> list, final int from, final int size) {
        final int end = Math.min((from + size), list.size());
        return list.subList(from, end);
    }

}
