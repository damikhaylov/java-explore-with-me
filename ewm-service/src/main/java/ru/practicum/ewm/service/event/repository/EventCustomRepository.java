package ru.practicum.ewm.service.event.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventCustomRepository {

    List<EventWithRequestsCount> findFilteredEventsWithRequestsCountList(BooleanBuilder predicate,
                                                                         boolean onlyAvailable,
                                                                         boolean sortedByDate);

    Page<EventWithRequestsCount> findFilteredEventsWithRequestsCountPage(BooleanBuilder builder, boolean onlyAvailable,
                                                                         boolean sortedByDate, PageRequest pageRequest);

    List<EventWithRequestsCount> findEventsWithRequestsCountByIds(Collection<Long> ids);

    Optional<EventWithRequestsCount> findEventWithRequestsCountById(long id);

    Map<Long, Long> findEventIdsToRequestsCountMapByIds(Collection<Long> ids);
}
