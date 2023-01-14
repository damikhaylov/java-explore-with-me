package ru.practicum.ewm.service.location;

import ru.practicum.ewm.service.location.dto.LocationDto;
import ru.practicum.ewm.service.location.dto.NewLocationDto;

public class LocationMapper {
    public static Location toLocation(NewLocationDto locationDto) {
        return new Location(
                0L,
                locationDto.getName(),
                locationDto.getLatitude(),
                locationDto.getLongitude(),
                locationDto.getRadius()
        );
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getLatitude(),
                location.getLongitude(),
                location.getRadius()
        );
    }
}
