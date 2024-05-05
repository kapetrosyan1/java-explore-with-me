package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatsClient {
    private final WebClient webClient;

    public StatsClient(@Value("${stats-server.url}") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public EndpointRequestDto create(EndpointRequestDto requestDto) {
        return webClient.post()
                .uri("/hit")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(EndpointRequestDto.class)
                .doOnSuccess(l -> log.info("Информация о посещении ресурса успешно сохранена"))
                .doOnError(e -> log.error("При выполнении запроса произошла непредвиденная ошибка"))
                .block();
    }

    public List<EndpointHitsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", URLEncoder.encode(start.format(formatter), StandardCharsets.UTF_8))
                        .queryParam("end", URLEncoder.encode(end.format(formatter), StandardCharsets.UTF_8))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<EndpointHitsDto>>() {})
                .doOnError(error -> log.error("При выполнении запроса произошла непредвиденная ошибка"))
                .block();
    }
}
