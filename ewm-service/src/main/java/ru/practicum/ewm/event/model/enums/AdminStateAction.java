package ru.practicum.ewm.event.model.enums;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.exception.exceptions.BadRequestException;

@Slf4j
public enum AdminStateAction {
    PUBLISH_EVENT,
    REJECT_EVENT;

    public static AdminStateAction stringToAdminAction(String action) {
        log.info("AdminStateActionEnum: Конвертация строки {} в Enum AdminStateAction", action);
        try {
            return valueOf(action);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown AdminStateAction: %s", action));
        }
    }
}
