package ru.practicum.ewm.service.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.service.CategoryPublicService;
import ru.practicum.ewm.service.pagination.CustomPageRequest;
import ru.practicum.ewm.service.category.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryPublicService categoryPublicService;

    @Autowired
    public CategoryPublicController(CategoryPublicService categoryPublicService) {
        this.categoryPublicService = categoryPublicService;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable("catId") Long categoryId) {
        return categoryPublicService.getCategory(categoryId);
    }

    @GetMapping()
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        return categoryPublicService.getAll(new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
