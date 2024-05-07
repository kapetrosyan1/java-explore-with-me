package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchFilter {
    private List<Long> initiators;
    private List<State> states;
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable = false;
}
