package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия подборки не может быть меньше 1 и больше 50 символов")
    private String title;
}
