package ru.practicum.ewm.service.event.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.QEvent;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.request.QRequest;
import ru.practicum.ewm.service.request.RequestStatus;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventCustomRepositoryImpl implements EventCustomRepository {

    @PersistenceContext
    private final EntityManager em;
    private final QEvent event = QEvent.event;
    private final QRequest request = QRequest.request;
    private final NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

    private JPQLQuery<Tuple> getQueryForFilteredEventsWithRequestsCount(BooleanBuilder filter, boolean onlyAvailable,
                                                                        boolean sortedByDate) {

        JPQLQuery<Tuple> query = new JPAQuery<>(em);

        query.select(event, request.id.count().as(count))
                .from(event)
                .leftJoin(request)
                .on(event.id.eq(request.event.id)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .where(filter)
                .groupBy(event.id);

        if (onlyAvailable) {
            query.having(event.participantLimit.gt(request.id.count()));
        }

        if (sortedByDate) {
            query.orderBy(event.eventDate.asc(), event.id.asc());
        } else {
            query.orderBy(event.id.asc());
        }

        return query;
    }

    @Override
    public List<EventWithRequestsCount> findFilteredEventsWithRequestsCountList(BooleanBuilder filter,
                                                                                boolean onlyAvailable,
                                                                                boolean sortedByDate) {

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCount(filter, onlyAvailable, sortedByDate);
        List<Tuple> tupleList = query.fetch();

        return tupleList.stream().map(x -> new EventWithRequestsCount(x.get(event), x.get(count)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<EventWithRequestsCount> findFilteredEventsWithRequestsCountPage(BooleanBuilder filter,
                                                                                boolean onlyAvailable,
                                                                                boolean sortedByDate,
                                                                                PageRequest pageRequest) {

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCount(filter, onlyAvailable, sortedByDate);

        Querydsl querydsl = new Querydsl(em, (new PathBuilderFactory()).create(Event.class));
        long totalElements = query.fetchCount();
        List<Tuple> tupleList = querydsl.applyPagination(pageRequest, query).fetch();
        Page<Tuple> tuplePage = new PageImpl<>(tupleList, pageRequest, totalElements);

        return tuplePage.map(x -> new EventWithRequestsCount(x.get(event), x.get(count)));
    }

    @Override
    public List<EventWithRequestsCount> findEventsWithRequestsCountByIds(Collection<Long> ids) {

        BooleanBuilder filter = new BooleanBuilder();
        filter.and(event.id.in(ids));

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCount(filter, false, false);
        List<Tuple> tupleList = query.fetch();

        return tupleList.stream().map(x -> new EventWithRequestsCount(x.get(event), x.get(count)))
                .collect(Collectors.toList());
    }


    @Override
    public Optional<EventWithRequestsCount> findEventWithRequestsCountById(long id) {

        BooleanBuilder filter = new BooleanBuilder();
        filter.and(event.id.eq(id));

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCount(filter, false, false);
        Tuple result = query.fetchOne();

        return (result != null)
                ? Optional.of(new EventWithRequestsCount(result.get(event), result.get(count)))
                : Optional.empty();
    }

    @Override
    public Map<Long, Long> findEventIdsToRequestsCountMapByIds(Collection<Long> ids) {

        JPQLQuery<Tuple> query = new JPAQuery<>(em);

        query.select(event.id, request.id.count().as(count))
                .from(event)
                .leftJoin(request)
                .on(event.id.eq(request.event.id)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .where(event.id.in(ids))
                .groupBy(event.id);

        List<Tuple> tupleList = query.fetch();

        return tupleList.stream().collect(Collectors.toMap(x -> x.get(0, Long.class), x -> x.get(count)));
    }

}
