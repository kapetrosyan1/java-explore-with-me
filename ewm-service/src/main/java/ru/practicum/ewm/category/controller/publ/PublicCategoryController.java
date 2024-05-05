package ru.practicum.ewm.category.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("PublicCategoryController: GET запрос по endpoint /categories, params from={}, size={}",
                from, size);
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto findById(@PathVariable @Positive Long catId) {
        log.info("PublicCategoryController: GET запрос по endpoint /categories/{}", catId);
        return categoryService.findById(catId);
    }
}
