package ru.practicum.ewm.service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationDto {
    @NotBlank
    @Size(min = 3, max = 255)
    private String name;
    @NotNull
    @Min(-90)
    @Max(90)
    private float latitude;
    @NotNull
    @Min(-180)
    @Max(180)
    private float longitude;
    @NotNull
    @PositiveOrZero
    private float radius;
}
