package ru.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    Set<Long> events;
    Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия подборки не может быть меньше 1 и больше 50 символов")
    String title;
}
