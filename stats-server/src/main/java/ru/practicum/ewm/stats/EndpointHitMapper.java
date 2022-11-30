package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.EndpointHitDto;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto dto) {
        return new EndpointHit(
                0,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }
}
