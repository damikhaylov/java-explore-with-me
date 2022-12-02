package ru.practicum.ewm.service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationDto {
    @NotNull
    private Long id;
    @Size(min = 3, max = 255)
    private String name;
    @Min(-90)
    @Max(90)
    private Float latitude;
    @Min(-180)
    @Max(180)
    private Float longitude;
    @PositiveOrZero
    private Float radius;
}
