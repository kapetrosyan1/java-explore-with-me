package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "длина аннотации не может быть меньше 20 и больше 2000 символов")
    private String annotation;
    @Positive
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "длина описания не может быть меньше 20 и больше 7000 символов")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120, message = "длина названия события не может быть меньше 3 и больше 120")
    private String title;
}
