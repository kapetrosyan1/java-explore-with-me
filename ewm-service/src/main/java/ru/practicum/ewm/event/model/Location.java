package ru.practicum.ewm.event.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Location {
    private float lat;
    private float lon;
}
