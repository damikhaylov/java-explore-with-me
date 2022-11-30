package ru.practicum.ewm.service.compilation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.service.CompilationPublicService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @Autowired
    public CompilationPublicController(CompilationPublicService compilationPublicService) {
        this.compilationPublicService = compilationPublicService;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        return compilationPublicService.getCompilation(compId);
    }

    @GetMapping()
    public List<CompilationDto> getCompilations(@RequestParam Optional<Boolean> pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return compilationPublicService.getCompilations(pinned, new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
