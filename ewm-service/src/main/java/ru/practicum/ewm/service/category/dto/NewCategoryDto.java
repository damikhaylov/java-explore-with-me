package ru.practicum.ewm.service.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String name;
}
