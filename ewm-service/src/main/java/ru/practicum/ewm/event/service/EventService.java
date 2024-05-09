package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.comment.dto.CommentResultDto;
import ru.practicum.ewm.event.comment.dto.NewCommentDto;
import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createNewEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> privateGetUsersEvents(Long userId, int from, int size);

    EventFullDto privateUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto privateFindEventById(Long userId, Long eventId);

    List<EventFullDto> adminFindAllWithSpecs(List<Long> userIds, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> publicFindAllWithSpecs(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               HttpServletRequest request);

    EventFullDto publicFindById(Long eventId, HttpServletRequest request);

    CommentResultDto privateCreateComment(NewCommentDto newCommentDto, Long authorId, Long eventId);

    CommentResultDto privateUpdateComment(NewCommentDto updateCommentDto, Long commentId, Long userId);

    void privateDeleteComment(Long userId, Long commentId);

    void adminDeleteComment(Long commentId);

    List<CommentResultDto> privateFindAllUsersComments(Long commentId, int from, int size);

    CommentResultDto adminFindCommentById(Long commentId);
}
