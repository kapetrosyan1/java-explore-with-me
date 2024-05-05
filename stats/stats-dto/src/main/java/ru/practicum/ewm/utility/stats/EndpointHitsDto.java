package ru.practicum.ewm.utility.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitsDto {
    private String app;
    private String uri;
    private Long hits;
}
