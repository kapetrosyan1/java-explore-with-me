package ru.practicum.ewm.event.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@Data
public class CommentResultDto {
    private Long id;
    private String authorName;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
}
