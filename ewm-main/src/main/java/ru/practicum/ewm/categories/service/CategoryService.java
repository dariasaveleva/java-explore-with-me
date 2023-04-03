package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(long id, CategoryDto categoryDto);

    void delete(Long id);




}
