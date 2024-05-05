package ru.practicum.ewm.event.model.enums;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.exceptions.BadRequestException;

@Slf4j
public enum UserStateAction {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static UserStateAction stringToUserAction(String action) {
        log.info("SortEnum: Конвертация строки {} в Enum UserStateAction", action);
        try {
            return valueOf(action);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown UserStateAction: %s", action));
        }
    }
}
