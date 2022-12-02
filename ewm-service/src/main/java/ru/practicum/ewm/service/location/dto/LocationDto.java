package ru.practicum.ewm.service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private long id;
    private String name;
    private float latitude;
    private float longitude;
    private float radius;
}
