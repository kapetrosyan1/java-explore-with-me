package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.validation.ValidEventDate;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEvent {
    @Size(min = 20, max = 2000)
    String annotation;
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    @Size(min = 3, max = 120)
    String title;
}
