package ru.practicum.ewm.stats.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

@Repository
public interface StatsCustomRepository {

    List<ViewStats> findEndpointHitsCustom(BooleanBuilder predicate, boolean uniqueIp);

}
