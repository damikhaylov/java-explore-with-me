package ru.practicum.ewm.stats.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.QEndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class StatsCustomRepositoryImpl implements StatsCustomRepository {

    @PersistenceContext
    private final EntityManager em;



    @Override
    public List<ViewStats> findEndpointHitsCustom(BooleanBuilder filter, boolean uniqueIp) {
        QEndpointHit endpointHit = QEndpointHit.endpointHit;
        NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

        JPQLQuery<Tuple> query = new JPAQuery<>(em);

        if (uniqueIp) {
            query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.countDistinct().as(count));
        } else {
            query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.count().as(count));
        }

        query.from(endpointHit).where(filter).groupBy(endpointHit.app, endpointHit.uri);

        return query.fetch().stream()
                .map(x -> new ViewStats(x.get(0, String.class), x.get(1, String.class), x.get(count)))
                .collect(Collectors.toList());
    }
}
