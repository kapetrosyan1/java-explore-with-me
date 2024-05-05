package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.service.StatsServerService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsServerController {
    private final StatsServerService service;

    @PostMapping("/hit")
    public EndpointRequestDto create(@RequestBody @Valid EndpointRequestDto requestDto) {
        log.info("StatServerController: запрос на добавление EndpointHit {}", requestDto.toString());
        return service.create(requestDto);
    }

    @GetMapping("/stats")
    public List<EndpointHitsDto> getHits(@RequestParam String start,
                                         @RequestParam String end,
                                         @RequestParam (required = false) List<String> uris,
                                         @RequestParam (defaultValue = "false") Boolean unique) {
        log.info("StatServerController: запрос на получение информации о просмотрах за период с {} по {}, uris = {}," +
                "unique = {}", start, end, uris, unique);
        return service.getHits(start, end, uris, unique);
    }
}
