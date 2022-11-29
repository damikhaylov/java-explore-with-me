package ru.practicum.ewm.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.client.StatsClient;
import ru.practicum.ewm.service.client.dto.EndpointHitDto;
import ru.practicum.ewm.service.client.dto.ViewStats;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatsExchanger {

    private final StatsClient statsClient;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${stats-server.url}")
    private String statsServerUrl;
    private final String eventEndpoint = "/events";
    private final boolean uniqueIpInViewStats = false;
    private final LocalDateTime minViewTimestamp =
            LocalDateTime.of(2022, 1, 1, 0, 0);
    private final LocalDateTime maxViewTimestamp =
            LocalDateTime.of(2200, 1, 1, 0, 0);

    @Autowired
    public StatsExchanger(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    public void saveStat(String uri, String ip) {
        statsClient.saveEndpointHit(URI.create(statsServerUrl),
                new EndpointHitDto(appName, uri, ip, LocalDateTime.now()));
    }

    public EventFullDto toEventFullDtoWithStats(Event event, long requestsCount) {
        Map<Long, Long> eventIdsToViewsCounts =
                getEventIdsToViewsCountsMapFromStatsServerByEventIds(Collections.singleton(event.getId()));

        return EventMapper.toFullDto(event, requestsCount,
                Optional.ofNullable(eventIdsToViewsCounts.get(event.getId())).orElse(0L));
    }

    public EventFullDto toEventFullDtoWithStats(EventWithRequestsCount eventWithRequestsCount) {
        return toEventFullDtoWithStats(eventWithRequestsCount.getEvent(), eventWithRequestsCount.getRequestCount());
    }

    public List<EventShortDto> toEventShortDtoListWithStats(List<EventWithRequestsCount> eventsWithRequestsCount) {
        if (eventsWithRequestsCount.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> eventIdsToViewsCounts = getEventIdsToViewsCountsMapFromStatsServerByEventIds(
                eventsWithRequestsCount.stream().map(x -> x.getEvent().getId()).collect(Collectors.toSet()));

        return eventsWithRequestsCount.stream()
                .map(x -> EventMapper.toShortDto(x.getEvent(), x.getRequestCount(),
                        Optional.ofNullable(eventIdsToViewsCounts.get(x.getEvent().getId())).orElse(0L)))
                .collect(Collectors.toList());
    }

    public List<EventFullDto> toEventFullDtoListWithStats(List<EventWithRequestsCount> eventsWithRequestsCount) {
        if (eventsWithRequestsCount.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> eventIdsToViewsCounts = getEventIdsToViewsCountsMapFromStatsServerByEventIds(
                eventsWithRequestsCount.stream().map(x -> x.getEvent().getId()).collect(Collectors.toSet()));

        return eventsWithRequestsCount.stream()
                .map(x -> EventMapper.toFullDto(x.getEvent(), x.getRequestCount(),
                        Optional.ofNullable(eventIdsToViewsCounts.get(x.getEvent().getId())).orElse(0L)))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getEventIdsToViewsCountsMapFromStatsServerByEventIds(Set<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> endpointsToIds = eventIds.stream()
                .collect(Collectors.toMap(x -> eventEndpoint + "/" + x.toString(), Function.identity()));

        String urisRequestParameter = String.join(",", endpointsToIds.keySet());

        List<ViewStats> viewStatsList = statsClient.getViewStats(URI.create(statsServerUrl),
                minViewTimestamp, maxViewTimestamp, Optional.of(urisRequestParameter), uniqueIpInViewStats);

        return viewStatsList.stream().collect(Collectors
                .toMap(x -> endpointsToIds.get(x.getUri()), ViewStats::getHits));
    }
}
