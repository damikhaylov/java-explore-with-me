package ru.practicum.ewm.service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private Set<CompilationEventShortDto> events = new HashSet<>();
}
