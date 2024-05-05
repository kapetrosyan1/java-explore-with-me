package ru.practicum.ewm.user.participation.request.dto;

import lombok.Data;
import ru.practicum.ewm.user.participation.request.model.enums.ParticipationStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private ParticipationStatus status;
}
