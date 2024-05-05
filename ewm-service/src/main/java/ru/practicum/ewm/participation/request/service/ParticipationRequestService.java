package ru.practicum.ewm.participation.request.service;

import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participation.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> findByRequester(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findAllByEventAndInitiator(Long userId, Long eventId);
    EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                             Long eventId,
                                                             EventRequestStatusUpdateRequest updateRequest);
}
