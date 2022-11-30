package ru.practicum.ewm.service.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.category.CategoryMapper;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryAdminService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        Category category = CategoryMapper.toNewCategory(categoryDto);
        Category createdCategory = categoryRepository.save(category);
        log.info("Creating a new category - a user with id={} has been created", createdCategory.getId());
        return CategoryMapper.toCategoryDto(createdCategory);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        String operationNameForLogging = "Updating a new category ";
        Category category = CategoryMapper.toCategory(categoryDto);
        if (!categoryRepository.existsById(category.getId())) {
            throw new IdWasNotFoundException(
                    String.format("%s error - category id=%d was not found",
                            operationNameForLogging, category.getId()));
        }
        Category updatedCategory = categoryRepository.save(category);
        log.info("{} - a category id={} has been updated", operationNameForLogging, updatedCategory.getId());
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        log.info("Deleting a category id={} - a category has been deleted", id);
    }

}
