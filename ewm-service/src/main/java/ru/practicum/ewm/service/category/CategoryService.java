package ru.practicum.ewm.service.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.exception.NonExistentIdException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        Category createdCategory = categoryRepository.save(category);
        log.info("A category with id={} has been created", createdCategory.getId());
        return CategoryMapper.toCategoryDto(createdCategory);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        if (!categoryRepository.existsById(category.getId())) {
            throw new NonExistentIdException(
                    String.format("Category with id=%d was not found for update", category.getId()));
        }
        Category updatedCategory = categoryRepository.save(category);
        log.info("A category with id={} has been updated", updatedCategory.getId());
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id={}", id);
        categoryRepository.deleteById(id);
    }

    public CategoryDto getCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return CategoryMapper.toCategoryDto(category.orElseThrow(
                () -> new NonExistentIdException(String.format("Category with id=%d was not found", id))));
    }

    public List<CategoryDto> getAll(PageRequest pageRequest) {
        Page<Category> categories = categoryRepository.findAll(pageRequest);
        log.info("Getting a page of a complete list of categories containing {} items",
                categories.getTotalElements());
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

}
