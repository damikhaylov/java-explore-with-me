package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.EndpointHit;

@Repository
public interface StatsRepository extends StatsCustomRepository, JpaRepository<EndpointHit, Long> {

}
