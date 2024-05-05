package ru.practicum.ewm.user.participation.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.user.participation.request.model.enums.ParticipationStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    ParticipationStatus status = ParticipationStatus.PENDING;
}
