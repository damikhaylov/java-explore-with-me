package ru.practicum.ewm.service.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.helper.CustomPageRequest;
import ru.practicum.ewm.service.category.CategoryDto;
import ru.practicum.ewm.service.category.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable("catId") Long categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @GetMapping()
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(new CustomPageRequest(from, size, Sort.unsorted()));
    }

}
