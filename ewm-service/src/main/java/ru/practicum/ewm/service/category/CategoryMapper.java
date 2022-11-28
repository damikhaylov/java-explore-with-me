package ru.practicum.ewm.service.category;

import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category toNewCategory(NewCategoryDto categoryDto) {
        return new Category(
                0L,
                categoryDto.getName()
        );
    }
    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
