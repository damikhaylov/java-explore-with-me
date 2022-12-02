package ru.practicum.ewm.service.location.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.EventState;
import ru.practicum.ewm.service.event.QEvent;
import ru.practicum.ewm.service.event.StatsExchanger;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.event.repository.SortState;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;
import ru.practicum.ewm.service.location.Location;
import ru.practicum.ewm.service.location.LocationMapper;
import ru.practicum.ewm.service.location.dto.LocationDto;
import ru.practicum.ewm.service.location.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class LocationPublicService {
    private final LocationRepository locationRepository;
    private final StatsExchanger statsExchanger;

    @Autowired
    public LocationPublicService(LocationRepository locationRepository, StatsExchanger statsExchanger) {
        this.locationRepository = locationRepository;
        this.statsExchanger = statsExchanger;
    }

    public LocationDto getLocation(Long id) {
        String operationNameForLogging = "Getting info about a location id=" + id + " by public";
        Optional<Location> location = locationRepository.findById(id);
        if (location.isEmpty()) {
            throw new IdWasNotFoundException(String.format(" error - location with id=%d was not found", id));
        }
        log.info("{} - done", operationNameForLogging);
        return LocationMapper.toLocationDto(location.get());
    }

    public List<LocationDto> getAllLocations(PageRequest pageRequest) {
        Page<Location> locations = locationRepository.findAll(pageRequest);
        log.info("Listing locations for public - a page of a complete list of {} items has been compiled",
                locations.getTotalElements());
        return locations.stream().map(LocationMapper::toLocationDto).collect(Collectors.toList());
    }

    public List<EventShortDto> getFilteredEventsByLocationId(long locationId,
                                                             Optional<String> text,
                                                             Optional<Long[]> categories,
                                                             Optional<Boolean> paid,
                                                             Optional<LocalDateTime> rangeStart,
                                                             Optional<LocalDateTime> rangeEnd,
                                                             boolean onlyAvailable,
                                                             Optional<SortState> sort,
                                                             PageRequest pageRequest) {

        String operationNameForLogging = "Listing filtering events by location for public";
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
                    locationRepository.findFilteredEventsWithRequestsCountListByLocationId(locationId, filter,
                            onlyAvailable, false);
            eventShortDtoList = statsExchanger.toEventShortDtoListWithStats(eventsWithRequestsCount);
            eventShortDtoList.sort(Comparator
                    .comparing(EventShortDto::getViews, Comparator.reverseOrder()).thenComparing(EventShortDto::getId));
            competeListSize = eventShortDtoList.size();
            eventShortDtoList = getPageSizedSublist(eventShortDtoList, (int) pageRequest.getOffset(),
                    pageRequest.getPageSize());
        } else {
            boolean sortedByDate = sort.isPresent() && sort.get().equals(SortState.EVENT_DATE);
            Page<EventWithRequestsCount> eventsWithRequestsCount =
                    locationRepository.findFilteredEventsWithRequestsCountPageByLocationId(locationId, filter,
                            onlyAvailable, sortedByDate, pageRequest);
            competeListSize = eventsWithRequestsCount.getTotalElements();
            eventShortDtoList =
                    statsExchanger.toEventShortDtoListWithStats(eventsWithRequestsCount.toList());
        }

        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, competeListSize);
        return eventShortDtoList;
    }

    private <T> List<T> getPageSizedSublist(List<T> list, final int from, final int size) {
        final int end = Math.min((from + size), list.size());
        return list.subList(from, end);
    }

}
