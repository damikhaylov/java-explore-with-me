package ru.practicum.ewm.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.event.Event;

public interface EventRepository extends EventCustomRepository, JpaRepository<Event, Long> {

}
