package ru.practicum.ewm.service.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.helper.CreateContext;
import ru.practicum.ewm.service.helper.UpdateContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @NotNull(groups = {UpdateContext.class})
    private Long id;
    @NotBlank(groups = {CreateContext.class, UpdateContext.class})
    private String name;
}
