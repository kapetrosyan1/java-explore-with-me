package ru.practicum.ewm.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.JpaCategoryRepository;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.exception.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final JpaCategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.info("CategoryService: добавление категории {}", newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.fromCategoryCreationDto(
                null, newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        log.info("CategoryService: обновление категории с id {}, новые данные: {}", id, newCategoryDto);
        categoryExists(id);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.fromCategoryCreationDto(
                id, newCategoryDto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("CategoryService: удаление категории с id {}", id);
        categoryExists(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        log.info("CategoryService: поиск всех категорий. Параметры страницы from {}, size {}", from, size);
        int page = from / size;
        Sort sort = Sort.by(ASC, "id");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Category> categories = categoryRepository.findAll(pageRequest);

        if (!categories.hasContent()) {
            return new ArrayList<>();
        }

        return categories.getContent().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        log.info("CategoryService: получение категории с id {}", id);
        return CategoryMapper.toCategoryDto(categoryExists(id));
    }

    private Category categoryExists(Long id) {
        log.info("CategoryService: проверяется существует ли в базе обновляемая категория");
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(
                "Category with id=%d was not found", id)));
    }
}
