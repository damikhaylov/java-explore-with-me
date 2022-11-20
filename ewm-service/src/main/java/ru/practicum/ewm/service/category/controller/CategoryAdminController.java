package ru.practicum.ewm.service.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.CategoryDto;
import ru.practicum.ewm.service.category.CategoryService;
import ru.practicum.ewm.service.helper.CreateContext;
import ru.practicum.ewm.service.helper.UpdateContext;

@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public CategoryDto createCategory(@RequestBody @Validated({CreateContext.class}) CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping()
    public CategoryDto updateCategory(@RequestBody @Validated({UpdateContext.class}) CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

}
