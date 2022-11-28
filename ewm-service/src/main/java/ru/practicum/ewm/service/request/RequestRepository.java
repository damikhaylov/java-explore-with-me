package ru.practicum.ewm.service.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterAndEvent(User requester, Event event);

    int countByEventAndStatus(Event event, RequestStatus status);

    List<Request> findByRequester(User requester);

    List<Request> findByEvent(Event event);

    @Modifying
    @Query("update Request r set r.status = 'CANCELED' where (r.event.id = :eventId and r.status = 'PENDING')")
    void rejectAllPendingRequestsForEvent(@Param("eventId") long eventId);

}
