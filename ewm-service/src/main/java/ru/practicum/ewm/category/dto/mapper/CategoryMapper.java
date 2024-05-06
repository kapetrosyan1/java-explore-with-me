package ru.practicum.ewm.category.dto.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

@Slf4j
public class CategoryMapper {
    public static Category fromCategoryCreationDto(Long id, NewCategoryDto newCategoryDto) {
        log.info("CategoryMapper: конвертация NewCategoryDto в Category");
        Category category = new Category();
        if (id != null) {
            category.setId(id);
        }
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        log.info("CategoryMapper: конвертация Category в CategoryDto");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
