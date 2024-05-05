package ru.practicum.ewm.participation.request.dto.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.participation.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.request.model.ParticipationRequest;

@UtilityClass
@Slf4j
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        log.info("RequestMapper: конвертация ParticipationRequest в ParticipationRequestDto");
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setStatus(request.getStatus());
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }
}
