package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.mapper.CategoryMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        log.info("Найден список категорий");
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Категория не существует");
        });
        log.info("Найдена категория");
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = repository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Создана категория c id {}", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(long catId, CategoryDto categoryDto) {
        Category category = repository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException("Категория для обновления не найдена");
        });
        if (categoryDto.getName().equals(category.getName())) {
            throw new ConflictException("Same category name");
        }
        category.setName(categoryDto.getName());
        log.info("Категория обновлена");
        return CategoryMapper.toCategoryDto(repository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Категория не найдена");
        });
        log.info("Удалена категория с id{}", id);
        repository.deleteById(id);
    }
}
