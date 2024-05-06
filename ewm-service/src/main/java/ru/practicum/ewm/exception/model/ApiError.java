package ru.practicum.ewm.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@AllArgsConstructor
@Data
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
}
