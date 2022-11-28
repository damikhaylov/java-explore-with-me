package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.EndpointHit;

public interface StatsRepository extends StatsCustomRepository, JpaRepository<EndpointHit, Long>{
//    SELECT app, uri, COUNT(ip) AS hits FROM endpoint_hits
//    GROUP BY app, uri
//    ORDER BY uri;
}
