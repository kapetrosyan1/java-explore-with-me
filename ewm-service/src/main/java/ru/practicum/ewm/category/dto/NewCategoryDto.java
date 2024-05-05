package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryCreationDto {
    @NotBlank
    @Size(max = 255)
    private String name;
}
