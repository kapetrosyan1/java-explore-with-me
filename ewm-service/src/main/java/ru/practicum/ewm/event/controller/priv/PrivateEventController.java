package ru.practicum.ewm.event.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.comment.dto.CommentResultDto;
import ru.practicum.ewm.event.comment.dto.NewCommentDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participation.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.request.service.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable @Positive Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("PrivateEventController: GET запрос по endpoint /users/{}/events", userId);
        return eventService.privateGetUsersEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("PrivateEventController: POST запрос по endpoint /users/{}/events", userId);
        return eventService.createNewEvent(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        log.info("PrivateEventController: GET запрос по endpoint /users/{}/events/{}", userId, eventId);
        return eventService.privateFindEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest userRequest) {
        log.info("PrivateEventController: PATCH запрос по endpoint /users/{}/events/{}", userId, eventId);
        return eventService.privateUpdateEvent(userId, eventId, userRequest);
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable @Positive Long userId,
                                                              @PathVariable @Positive Long eventId) {
        log.info("PrivateEventController: GET запрос по endpoint /users/{}/events/{}/requests", userId, eventId);
        return requestService.findAllByEventAndInitiator(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult responseToRequests(@PathVariable @Positive Long userId,
                                                             @PathVariable @Positive Long eventId,
                                                             @RequestBody @Valid
                                                             EventRequestStatusUpdateRequest updateRequest) {
        log.info("PrivateEventController: PATCH запрос по endpoint /users/{}/events/{}/requests",
                userId, eventId);
        return requestService.updateRequestStatus(userId, eventId, updateRequest);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResultDto createComment(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long eventId,
                                          @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("PrivateEventController: POST запрос по endpoint /users/{}/events/{}/comments", userId, eventId);
        return eventService.privateCreateComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentResultDto updateComment(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long commentId,
                                          @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("PrivateEventController: PATCH запрос по endpoint /users/{}/events/comments/{}",
                userId, commentId);
        return eventService.privateUpdateComment(newCommentDto, commentId, userId);
    }

    @GetMapping("/comments")
    public List<CommentResultDto> findAllUserComments(@PathVariable @Positive Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("PrivateEventController: GET запрос по endpoint /users/{}/events/comments", userId);
        return eventService.privateFindAllUsersComments(userId, from, size);
    }

    @DeleteMapping("/comments/delete/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAuthor(@PathVariable @Positive Long userId,
                                      @PathVariable @Positive Long commentId) {
        log.info("PrivateEventController: DELETE запрос по endpoint /users/{}/events/comments/delete/{}",
                userId, commentId);
        eventService.privateDeleteComment(userId, commentId);
    }
}
