package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.exceptions.BadRequestException;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.utility.EndpointDtoMapper;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;
import ru.practicum.ewm.repository.JpaStatsServerRepository;
import ru.practicum.ewm.service.StatsServerService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {

    private final JpaStatsServerRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointRequestDto create(EndpointRequestDto requestDto) {
        log.info("StatsServerServiceImpl: сохранение в историю обращения {}", requestDto.toString());

        EndpointHit endpointHit = EndpointDtoMapper.toEndpointHit(requestDto, formatter);
        return EndpointDtoMapper.toEndpointRequestDto(repository.save(endpointHit), formatter);
    }

    @Override
    public List<EndpointHitsDto> getHits(String start, String end, List<String> uris, Boolean isUnique) {
        log.info("StatsServerServiceImpl: обработка запроса на получение статистики за период с {} по {}, uris = {}, " +
                "unique = {}", start, end, uris, isUnique);
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);

        if (endTime.isBefore(startTime)) {
            throw new BadRequestException("Стартовая дата диапазона не может быть после конечной даты");
        }

        if (uris == null || uris.size() == 0) {
            return isUnique ? repository.findDistinctHitsWithNoUrisList(startTime, endTime) :
                    repository.findHitsWithNoUrisList(startTime, endTime);
        } else {
            return isUnique ? repository.findDistinctHitsWithUrisList(startTime, endTime, uris) :
                    repository.findHitsWithUrisList(startTime, endTime, uris);
        }
    }
}
