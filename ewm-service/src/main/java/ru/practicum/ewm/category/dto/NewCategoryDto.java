package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия категории должна быть не менее 1 и не более 50 символов")
    private String name;
}
