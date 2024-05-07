package ru.practicum.ewm.event.model.enums;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.exceptions.BadRequestException;

@Slf4j
public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State stringToState(String state) {
        log.info("StateEnum: Конвертация строки {} в Enum State", state);
        try {
            return valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}
