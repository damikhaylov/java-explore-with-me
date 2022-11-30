package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.CompilationEventShortDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto dto, Set<Event> events) {
        return new Compilation(
                null,
                dto.getTitle(),
                dto.isPinned(),
                events
        );
    }

    public static CompilationDto toCompilationDtoWithoutEvents(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                new HashSet<>()
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  List<EventWithRequestsCount> eventsWithRequestsCount) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                new HashSet<>(eventsWithRequestsCount.stream().map(CompilationMapper::toEventShortDto)
                        .collect(Collectors.toList()))
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  Map<Long, Long> eventIdsRequestsCounts) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                new HashSet<>(compilation.getEvents().stream()
                        .map(x -> toEventShortDto(x, eventIdsRequestsCounts.get(x.getId())))
                        .collect(Collectors.toList()))
        );
    }

    public static CompilationEventShortDto toEventShortDto(Event event, long requestsCount) {
        return new CompilationEventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                new CompilationEventShortDto.CategoryDto(
                        event.getCategory().getId(),
                        event.getCategory().getName()),
                event.getEventDate(),
                event.getPaid(),
                new CompilationEventShortDto.UserShortDto(
                        event.getInitiator().getId(),
                        event.getInitiator().getName()),
                requestsCount,
                0
        );
    }

    public static CompilationEventShortDto toEventShortDto(EventWithRequestsCount eventWithRequestsCount) {
        Event event = eventWithRequestsCount.getEvent();
        long requestsCount = eventWithRequestsCount.getRequestCount();
        return toEventShortDto(event, requestsCount);
    }
}
