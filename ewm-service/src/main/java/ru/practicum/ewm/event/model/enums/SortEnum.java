package ru.practicum.ewm.event.model.enums;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.exceptions.BadRequestException;

@Slf4j
public enum SortEnum {
    EVENT_DATE,
    VIEWS;

    public static SortEnum stringToSort(String sort) {
        log.info("SortEnum: Конвертация строки {} в Enum Sort", sort);
        try {
            return valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown Sort: %s", sort));
        }
    }
}
