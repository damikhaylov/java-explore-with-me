package ru.practicum.ewm.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public void createEndpointHit(@RequestBody EndpointHitDto dto) {
        statsService.createEndpointHit(dto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getViews(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam Optional<String[]> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getUsers(start, end, uris, unique);
    }

}

