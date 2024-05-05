package ru.practicum.ewm.service;

import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;

import java.util.List;

public interface StatsServerService {
    EndpointRequestDto create(EndpointRequestDto requestDto);

    List<EndpointHitsDto> getHits(String start, String end, List<String> uris, Boolean unique);
}
