package ru.practicum.ewm.stats;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    public void createEndpointHit(EndpointHitDto dto) {
        String operationNameForLogging = "Creating a new endpoint hit";
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(dto);
        EndpointHit createdEndpointHit = statsRepository.save(endpointHit);
        log.info("{} - an endpoint hit id={} has been created", operationNameForLogging, createdEndpointHit.getId());
    }

    public List<ViewStats> getUsers(LocalDateTime start, LocalDateTime end, Optional<String[]> uris, boolean uniqueIp) {
        String operationNameForLogging = "Listing aggregated view statistics";
        QEndpointHit endpointHit = QEndpointHit.endpointHit;

        BooleanBuilder filter = new BooleanBuilder();
        filter.and(endpointHit.timestamp.between(start, end));
        uris.ifPresent(x -> filter.and(endpointHit.uri.in(Arrays.asList(x))));

        List<ViewStats> viewStatsList = statsRepository.findEndpointHitsCustom(filter, uniqueIp);
        log.info("{} - a list of {} items has been compiled", operationNameForLogging, viewStatsList.size());
        return viewStatsList;
    }
}
