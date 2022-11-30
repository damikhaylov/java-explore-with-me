package ru.practicum.ewm.service.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.SortState;
import ru.practicum.ewm.service.location.dto.LocationDto;
import ru.practicum.ewm.service.location.service.LocationPublicService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/locations")
public class LocationPublicController {
    private final LocationPublicService locationPublicService;

    @Autowired
    public LocationPublicController(LocationPublicService locationPublicService) {
        this.locationPublicService = locationPublicService;
    }

    @GetMapping("/{locationId}")
    public LocationDto getCategory(@PathVariable Long locationId) {
        return locationPublicService.getLocation(locationId);
    }

    @GetMapping()
    public List<LocationDto> getAllLocations(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        return locationPublicService.getAllLocations(new CustomPageRequest(from, size, Sort.unsorted()));
    }

    @GetMapping("/{locationId}/events")
    public List<EventShortDto> getFilteredEventsByLocationId(@PathVariable Long locationId,
                                                             @RequestParam Optional<String> text,
                                                             @RequestParam Optional<Long[]> categories,
                                                             @RequestParam Optional<Boolean> paid,
                                                             @RequestParam
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                             Optional<LocalDateTime> rangeStart,
                                                             @RequestParam
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                             Optional<LocalDateTime> rangeEnd,
                                                             @RequestParam(defaultValue = "false")
                                                             boolean onlyAvailable,
                                                             @RequestParam Optional<SortState> sort,
                                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return locationPublicService.getFilteredEventsByLocationId(locationId, text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
