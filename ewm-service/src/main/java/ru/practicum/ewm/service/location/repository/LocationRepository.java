package ru.practicum.ewm.service.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.location.Location;

@Repository
public interface LocationRepository extends LocationCustomRepository, JpaRepository<Location, Long> {

}
