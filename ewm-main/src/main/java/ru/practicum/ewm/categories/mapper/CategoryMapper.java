package ru.practicum.ewm.categories.mapper;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }
}
