package ru.practicum.ewm.service.location.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.event.QEvent;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;
import ru.practicum.ewm.service.location.QLocation;
import ru.practicum.ewm.service.request.QRequest;
import ru.practicum.ewm.service.request.RequestStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository {

    @PersistenceContext
    private final EntityManager em;
    private final QEvent event = QEvent.event;
    private final QRequest request = QRequest.request;

    private final QLocation location = QLocation.location;
    private final NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

    private JPQLQuery<Tuple> getQueryForFilteredEventsWithRequestsCountByLocationId(long locationId,
                                                                                    BooleanBuilder filter,
                                                                                    boolean onlyAvailable,
                                                                                    boolean sortedByDate) {

        BooleanExpression inLocation = Expressions.numberTemplate(float.class,
                "distance_from_location({0}, {1}, {2}, {3})",
                event.latitude, event.longitude, location.latitude, location.longitude).loe(location.radius);

        JPQLQuery<Tuple> query = new JPAQuery<>(em);

        query.select(event, request.id.count().as(count))
                .from(event, location)
                .leftJoin(request)
                .on(event.id.eq(request.event.id)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .where(location.id.eq(locationId), inLocation, filter)
                .groupBy(event.id);

        if (onlyAvailable) {
            query.having(event.participantLimit.gt(request.id.count()).or(event.participantLimit.eq(0L)));
        }

        if (sortedByDate) {
            query.orderBy(event.eventDate.asc(), event.id.asc());
        } else {
            query.orderBy(event.id.asc());
        }

        return query;
    }

    @Override
    public List<EventWithRequestsCount> findFilteredEventsWithRequestsCountListByLocationId(long locationId,
                                                                                            BooleanBuilder filter,
                                                                                            boolean onlyAvailable,
                                                                                            boolean sortedByDate) {

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCountByLocationId(locationId, filter,
                onlyAvailable, sortedByDate);
        List<Tuple> tupleList = query.fetch();

        return tupleList.stream().map(x -> new EventWithRequestsCount(x.get(event), x.get(count)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<EventWithRequestsCount> findFilteredEventsWithRequestsCountPageByLocationId(long locationId,
                                                                                            BooleanBuilder filter,
                                                                                            boolean onlyAvailable,
                                                                                            boolean sortedByDate,
                                                                                            PageRequest pageRequest) {

        JPQLQuery<Tuple> query = getQueryForFilteredEventsWithRequestsCountByLocationId(locationId, filter,
                onlyAvailable, sortedByDate);

        Querydsl querydsl = new Querydsl(em, (new PathBuilderFactory()).create(Event.class));
        long totalElements = query.fetchCount();
        List<Tuple> tupleList = querydsl.applyPagination(pageRequest, query).fetch();
        Page<Tuple> tuplePage = new PageImpl<>(tupleList, pageRequest, totalElements);

        return tuplePage.map(x -> new EventWithRequestsCount(x.get(event), x.get(count)));
    }

}
