package ru.practicum.ewm.service.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.category.CategoryMapper;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.exception.IdWasNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryPublicService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryPublicService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto getCategory(Long id) {
        String operationNameForLogging = "Getting info about an category id=" + id + " by public";
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            throw new IdWasNotFoundException(String.format(" error - category with id=%d was not found", id));
        }
        log.info("{} - done", operationNameForLogging);
        return CategoryMapper.toCategoryDto(category.get());
    }

    public List<CategoryDto> getAll(PageRequest pageRequest) {
        Page<Category> categories = categoryRepository.findAll(pageRequest);
        log.info("Listing categories for public - a page of a complete list of {} items has been compiled",
                categories.getTotalElements());
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

}
