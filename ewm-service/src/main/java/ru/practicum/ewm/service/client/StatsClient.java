package ru.practicum.ewm.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.service.client.dto.EndpointHitDto;
import ru.practicum.ewm.service.client.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@FeignClient(value = "ewm-stats-client", url = "http://dump.for.use.environment.variable")
public interface StatsClient {
    @RequestMapping(method = RequestMethod.GET, value = "/stats")
    List<ViewStats> getViewStats(URI baseUrl,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                 @RequestParam Optional<String> uris,
                                 @RequestParam boolean unique);

    @RequestMapping(method = RequestMethod.POST, value = "/hit", consumes = "application/json")
    void saveEndpointHit(URI baseUrl, EndpointHitDto endpointHitDto);

}
