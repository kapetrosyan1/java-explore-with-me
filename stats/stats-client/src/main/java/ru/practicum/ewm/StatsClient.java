package ru.practicum.ewm;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StatsClient {
    private final WebClient webClient;

    public StatsClient(String baseUrl) {
        webClient = WebClient.create(baseUrl);
    }

    public void create(EndpointRequestDto requestDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public ResponseEntity<List<EndpointHitsDto>> getHits(String start, String end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", URLEncoder.encode(start, StandardCharsets.UTF_8))
                        .queryParam("end", URLEncoder.encode(end, StandardCharsets.UTF_8))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .toEntityList(EndpointHitsDto.class)
                .block();
    }
}
