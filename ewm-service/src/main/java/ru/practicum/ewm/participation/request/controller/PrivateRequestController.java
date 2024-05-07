package ru.practicum.ewm.participation.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participation.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.request.service.ParticipationRequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> findUsersRequests(@PathVariable @Positive Long userId) {
        log.info("PrivateRequestController: GET запрос по endpoint /users/{}/requests", userId);
        return requestService.findByRequester(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @Positive Long userId,
                                                 @RequestParam @Positive Long eventId) {
        log.info("PrivateRequestController: POST запрос по endpoint /users/{}/requests, params eventId={}",
                userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) {
        log.info("PrivateRequestController: PATCH запрос по endpoint /users/{}/requests/{}/cancel",
                userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
