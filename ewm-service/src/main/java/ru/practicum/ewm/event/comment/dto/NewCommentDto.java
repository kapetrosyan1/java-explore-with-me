package ru.practicum.ewm.event.comment.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class NewCommentDto {
    @NotBlank
    @Length(min = 1, max = 1000, message = "Длина комментария не может превышать 1000 символов")
    private String text;
}
