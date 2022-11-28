package ru.practicum.ewm.service.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.Compilation;
import ru.practicum.ewm.service.compilation.CompilationMapper;
import ru.practicum.ewm.service.compilation.CompilationRepository;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CompilationPublicService {
    private final CompilationRepository compilationRepository;
    protected final EventRepository eventRepository;

    @Autowired
    public CompilationPublicService(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public CompilationDto getCompilation(long id) {
        String operationNameForLogging = String.format("Getting info about a compilation id=%d by public", id);

        Compilation compilation = getCompilationOrThrowException(id, operationNameForLogging);

        if (compilation.getEvents().isEmpty()) {
            log.info("{} - done", operationNameForLogging);
            return CompilationMapper.toCompilationDtoWithoutEvents(compilation);
        }

        Set<Long> eventIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> eventIdsRequestsCounts = eventRepository.findEventIdsToRequestsCountMapByIds(eventIds);

        log.info("{} - done", operationNameForLogging);
        return CompilationMapper.toCompilationDto(compilation, eventIdsRequestsCounts);
    }


    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, PageRequest pageRequest) {
        String operationNameForLogging = "Listing compilations for public ";

        Page<Compilation> compilations = (pinned.isPresent())
                ? compilationRepository.findByPinned(pinned.get(), pageRequest)
                : compilationRepository.findAll(pageRequest);

        Set<Long> eventIds = compilations.stream().flatMap(x -> x.getEvents().stream().map(Event::getId))
                .collect(Collectors.toSet());
        Map<Long, Long> eventIdsRequestsCounts = eventRepository.findEventIdsToRequestsCountMapByIds(eventIds);

        log.info("{} - a page of a complete list of {} items has been compiled",
                operationNameForLogging, compilations.getTotalElements());
        return compilations.stream().map(x -> CompilationMapper.toCompilationDto(x, eventIdsRequestsCounts))
                .collect(Collectors.toList());
    }

    protected Compilation getCompilationOrThrowException(long id, String operationNameForLogging) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new IdWasNotFoundException(
                        String.format("%s error - compilation id=%d was not found", operationNameForLogging, id)));
    }

}
