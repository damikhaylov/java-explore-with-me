package ru.practicum.ewm.service.location.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.dto.EventWithRequestsCount;

import java.util.List;

@Repository
public interface LocationCustomRepository {

    List<EventWithRequestsCount> findFilteredEventsWithRequestsCountListByLocationId(long locationId,
                                                                                     BooleanBuilder filter,
                                                                                     boolean onlyAvailable,
                                                                                     boolean sortedByDate);

    Page<EventWithRequestsCount> findFilteredEventsWithRequestsCountPageByLocationId(long locationId,
                                                                                     BooleanBuilder filter,
                                                                                     boolean onlyAvailable,
                                                                                     boolean sortedByDate,
                                                                                     PageRequest pageRequest);
}
