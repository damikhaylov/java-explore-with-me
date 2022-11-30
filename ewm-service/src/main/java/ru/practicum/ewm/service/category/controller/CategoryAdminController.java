package ru.practicum.ewm.service.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.service.CategoryAdminService;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;

@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryAdminService categoryAdminService;

    @Autowired
    public CategoryAdminController(CategoryAdminService categoryAdminService) {
        this.categoryAdminService = categoryAdminService;
    }

    @PostMapping()
    public CategoryDto createCategory(@RequestBody @Validated NewCategoryDto categoryDto) {
        return categoryAdminService.createCategory(categoryDto);
    }

    @PatchMapping()
    public CategoryDto updateCategory(@RequestBody @Validated CategoryDto categoryDto) {
        return categoryAdminService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        categoryAdminService.deleteCategory(categoryId);
    }

}
