package ru.practicum.ewm.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.Event;

@Repository
public interface EventRepository extends EventCustomRepository, JpaRepository<Event, Long> {

}
