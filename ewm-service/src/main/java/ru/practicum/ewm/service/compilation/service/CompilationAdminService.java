package ru.practicum.ewm.service.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.Compilation;
import ru.practicum.ewm.service.compilation.CompilationMapper;
import ru.practicum.ewm.service.compilation.CompilationRepository;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationAdminService(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        String operationNameForLogging = "Creating a new compilation";

        List<EventWithRequestsCount> eventsWithRequestsCount = eventRepository
                .findEventsWithRequestsCountByIds(dto.getEvents());
        List<Event> events = eventsWithRequestsCount.stream().map(EventWithRequestsCount::getEvent)
                .collect(Collectors.toList());

        Compilation compilation = CompilationMapper.toCompilation(dto, new HashSet<>(events));
        Compilation createdCompilation = compilationRepository.save(compilation);

        log.info("{} - a compilation id={} has been created", operationNameForLogging,
                createdCompilation.getId());
        return CompilationMapper.toCompilationDto(createdCompilation, eventsWithRequestsCount);
    }

    @Transactional
    public void deleteCompilation(long id) {
        String operationNameForLogging = "Deleting a compilation id=" + id;
        compilationRepository.deleteById(id);
        log.info("{} - a compilation id={} has been deleted", operationNameForLogging, id);
    }

    @Transactional
    public void addEventToCompilation(long compId, long eventId) {
        String operationNameForLogging = String.format("Adding an event=%d to the compilation id=%d", eventId, compId);
        Compilation compilation = getCompilationOrThrowException(compId, operationNameForLogging);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
        log.info("{} - an event id {}=has been added to the compilation id={}",
                operationNameForLogging, eventId, compId);
    }

    @Transactional
    public void deleteEventFromCompilation(long compId, long eventId) {
        String operationNameForLogging =
                String.format("Deleting an event=%d from the compilation id=%d", eventId, compId);
        Compilation compilation = getCompilationOrThrowException(compId, operationNameForLogging);
        Event event = getEventOrThrowException(eventId, operationNameForLogging);
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
        log.info("{} - an event id {}=has been deleted from the compilation id={}",
                operationNameForLogging, eventId, compId);
    }

    @Transactional
    public void pinCompilation(long id, boolean isPinned) {
        String operationNameForLogging = String.format("Pinning a compilation id=%d", id);
        Compilation compilation = getCompilationOrThrowException(id, operationNameForLogging);
        compilation.setPinned(isPinned);
        compilationRepository.save(compilation);
        log.info("{} - a compilation id={} has been pinned", operationNameForLogging, id);
    }

    private Compilation getCompilationOrThrowException(long id, String operationNameForLogging) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - compilation id=%d was not found", operationNameForLogging, id)));
    }

    private Event getEventOrThrowException(long id, String operationNameForLogging) {
        return eventRepository.findById(id).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - event id=%d was not found", operationNameForLogging, id)));
    }
}
