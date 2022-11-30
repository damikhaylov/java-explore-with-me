package ru.practicum.ewm.service.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.EventState;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.location.service.LocationAdminService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;
import ru.practicum.ewm.service.location.dto.NewLocationDto;
import ru.practicum.ewm.service.location.dto.LocationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin/locations")
public class LocationAdminController {
    private final LocationAdminService locationAdminService;

    @Autowired
    public LocationAdminController(LocationAdminService locationAdminService) {
        this.locationAdminService = locationAdminService;
    }

    @PostMapping
    public LocationDto createLocation(@RequestBody @Validated NewLocationDto locationDto) {
        return locationAdminService.createLocation(locationDto);
    }

    @PatchMapping
    public LocationDto updateLocation(@RequestBody @Validated LocationDto locationDto) {
        return locationAdminService.updateLocation(locationDto);
    }

    @DeleteMapping("/{locationId}")
    public void deleteLocation(@PathVariable Long locationId) {
        locationAdminService.deleteLocation(locationId);
    }

    @GetMapping()
    public List<LocationDto> getLocations(@RequestParam Optional<Long[]> ids,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size) {
        return locationAdminService.getLocations(ids, new CustomPageRequest(from, size, Sort.unsorted()));
    }

    @GetMapping("/{locationId}/events")
    public List<EventFullDto> getFilteredEventsByLocationId(@PathVariable Long locationId,
                                                            @RequestParam Optional<Long[]> users,
                                                            @RequestParam Optional<EventState[]> states,
                                                            @RequestParam Optional<Long[]> categories,
                                                            @RequestParam
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                            Optional<LocalDateTime> rangeStart,
                                                            @RequestParam
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                            Optional<LocalDateTime> rangeEnd,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "10") @Positive int size) {
        return locationAdminService.getFilteredEventsByLocationId(locationId, users, states, categories,
                rangeStart, rangeEnd, new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
