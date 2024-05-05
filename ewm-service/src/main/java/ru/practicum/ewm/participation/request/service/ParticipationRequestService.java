package ru.practicum.ewm.user.participation.request.service;

import ru.practicum.ewm.user.participation.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> findByRequester(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    
}
