package ru.practicum.ewm.stats.repository;

import com.querydsl.core.BooleanBuilder;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

public interface StatsCustomRepository {

    List<ViewStats> findEndpointHitsCustom(BooleanBuilder predicate, boolean uniqueIp);

}
