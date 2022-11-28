package ru.practicum.ewm.stats.repository;

import com.querydsl.core.BooleanBuilder;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsCustomRepository {

    List<ViewStats> findEndpointHitsCustom(BooleanBuilder predicate, boolean uniqueIp);

}
