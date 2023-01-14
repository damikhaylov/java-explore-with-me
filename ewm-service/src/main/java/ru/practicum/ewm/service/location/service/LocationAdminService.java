package ru.practicum.ewm.service.location.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;
import ru.practicum.ewm.service.location.Location;
import ru.practicum.ewm.service.location.LocationMapper;
import ru.practicum.ewm.service.location.dto.NewLocationDto;
import ru.practicum.ewm.service.location.dto.LocationDto;
import ru.practicum.ewm.service.location.dto.UpdateLocationDto;
import ru.practicum.ewm.service.location.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class LocationAdminService {
    private final LocationRepository locationRepository;
    private final StatsExchanger statsExchanger;

    @Autowired
    public LocationAdminService(LocationRepository locationRepository, StatsExchanger statsExchanger) {
        this.locationRepository = locationRepository;
        this.statsExchanger = statsExchanger;
    }

    @Transactional
    public LocationDto createLocation(NewLocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        Location createdLocation = locationRepository.save(location);
        log.info("Creating a new location - a location with id={} has been created", createdLocation.getId());
        return LocationMapper.toLocationDto(createdLocation);
    }

    @Transactional
    public LocationDto updateLocation(UpdateLocationDto locationDto) {
        String operationNameForLogging = "Updating a location id=" + locationDto.getId();

        Location location =  locationRepository.findById(locationDto.getId()).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - location with id=%d was not found",
                                operationNameForLogging, locationDto.getId())));

        Optional.ofNullable(locationDto.getName()).ifPresent(location::setName);
        Optional.ofNullable(locationDto.getLatitude()).ifPresent(location::setLatitude);
        Optional.ofNullable(locationDto.getLongitude()).ifPresent(location::setLongitude);
        Optional.ofNullable(locationDto.getRadius()).ifPresent(location::setRadius);

        Location updatedLocation = locationRepository.save(location);
        log.info("{} - a location id={} has been updated", operationNameForLogging, updatedLocation.getId());
        return LocationMapper.toLocationDto(updatedLocation);
    }

    @Transactional
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
        log.info("Deleting a location id={} - a location has been deleted", id);
    }

    public List<LocationDto> getLocations(Optional<Long[]> ids, PageRequest pageRequest) {
        String operationNameForLogging = "Listing locations for admin ";
        if (ids.isEmpty()) {
            Page<Location> locations = locationRepository.findAll(pageRequest);
            log.info("{} - a page of a complete list of {} items has been compiled",
                    operationNameForLogging, locations.getTotalElements());
            return locations.stream().map(LocationMapper::toLocationDto).collect(Collectors.toList());
        }
        List<Location> locations = locationRepository.findAllById(Arrays.asList(ids.get()));
        log.info("{} - a complete list of {} items has been compiled",
                operationNameForLogging, locations.size());
        return locations.stream().map(LocationMapper::toLocationDto).collect(Collectors.toList());
    }

    public List<EventFullDto> getFilteredEventsByLocationId(long locationId, Optional<Long[]> users,
                                                            Optional<EventState[]> states,
                                                            Optional<Long[]> categories,
                                                            Optional<LocalDateTime> rangeStart,
                                                            Optional<LocalDateTime> rangeEnd,
                                                            PageRequest pageRequest) {
        String operationNameForLogging = "Listing events by location for admin";
        QEvent event = QEvent.event;

        BooleanBuilder filter = new BooleanBuilder();
        categories.ifPresent(x -> filter.and(event.category.id.in(Arrays.asList(x))));
        states.ifPresent(x -> filter.and(event.state.in(Arrays.asList(x))));
        users.ifPresent(x -> filter.and(event.initiator.id.in(Arrays.asList(x))));
        rangeStart.ifPresent(x -> filter.and(event.eventDate.goe(x)));
        rangeEnd.ifPresent(x -> filter.and(event.eventDate.loe(x)));

        Page<EventWithRequestsCount> eventsWithRequestsCount =
                locationRepository.findFilteredEventsWithRequestsCountPageByLocationId(locationId, filter,
                        false, false, pageRequest);

        List<EventFullDto> eventFullDtoList =
                statsExchanger.toEventFullDtoListWithStats(eventsWithRequestsCount.toList());

        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, eventsWithRequestsCount.getTotalElements());
        return eventFullDtoList;
    }

}
