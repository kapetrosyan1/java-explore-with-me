package ru.practicum.ewm.utility;

import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointDtoMapper {
    public static EndpointHit toEndpointHit(EndpointRequestDto requestDto, DateTimeFormatter formatter) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setUri(requestDto.getUri());
        endpointHit.setIp(requestDto.getIp());
        endpointHit.setApp(requestDto.getApp());
        endpointHit.setCreated(LocalDateTime.parse(requestDto.getTimestamp(), formatter));
        return endpointHit;
    }

    public static EndpointRequestDto toEndpointRequestDto(EndpointHit endpointHit, DateTimeFormatter formatter) {
        EndpointRequestDto requestDto = new EndpointRequestDto();
        requestDto.setId(endpointHit.getId());
        requestDto.setApp(endpointHit.getApp());
        requestDto.setIp(endpointHit.getIp());
        requestDto.setTimestamp(endpointHit.getCreated().format(formatter));
        requestDto.setUri(endpointHit.getUri());
        return requestDto;
    }
}
