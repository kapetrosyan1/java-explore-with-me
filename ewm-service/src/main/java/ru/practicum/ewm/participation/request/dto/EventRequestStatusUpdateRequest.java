package ru.practicum.ewm.participation.request.dto;

import lombok.Data;
import ru.practicum.ewm.participation.request.model.enums.ParticipationStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private ParticipationStatus status;
}
