package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.EndpointHit;

public interface StatsRepository extends StatsCustomRepository, JpaRepository<EndpointHit, Long> {

}
