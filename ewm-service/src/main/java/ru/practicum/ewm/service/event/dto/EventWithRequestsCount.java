package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.Event;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventWithRequestsCount {
    private Event event;
    private long requestCount;
}
