package ru.practicum.ewm.participation.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.participation.request.model.enums.ParticipationStatus;

import java.time.LocalDateTime;

import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    ParticipationStatus status = ParticipationStatus.PENDING;
}
