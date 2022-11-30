package ru.practicum.ewm.service.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false, unique = true)
    private String name;
    @NotNull
    @Min(-90)
    @Max(90)
    @Column(nullable = false)
    private float latitude;
    @NotNull
    @Min(-180)
    @Max(180)
    @Column(nullable = false)
    private float longitude;
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private float radius;
}
