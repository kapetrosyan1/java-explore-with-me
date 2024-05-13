package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.JpaCategoryRepository;
import ru.practicum.ewm.event.comment.dto.CommentResultDto;
import ru.practicum.ewm.event.comment.dto.NewCommentDto;
import ru.practicum.ewm.event.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.event.comment.model.Comment;
import ru.practicum.ewm.event.comment.repository.JpaCommentRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.enums.AdminStateAction;
import ru.practicum.ewm.event.model.enums.SortEnum;
import ru.practicum.ewm.event.model.enums.State;
import ru.practicum.ewm.event.model.enums.UserStateAction;
import ru.practicum.ewm.event.repository.JpaEventRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.service.impl.specification.EventSpecification;
import ru.practicum.ewm.exception.exceptions.BadRequestException;
import ru.practicum.ewm.exception.exceptions.ForbiddenException;
import ru.practicum.ewm.exception.exceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.JpaUserRepository;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;
import ru.practicum.ewm.utility.stats.EndpointRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.ewm.Constant.DATE_TIME_FORMAT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final JpaEventRepository eventRepository;
    private final JpaCategoryRepository categoryRepository;
    private final JpaUserRepository userRepository;
    private final StatsClient statsClient;
    private final JpaCommentRepository commentRepository;
    private final EventSpecification eventSpecification = new EventSpecification();

    @Override
    @Transactional
    public EventFullDto createNewEvent(NewEventDto newEventDto, Long userId) {
        log.info("EventService PRIVATE: добавление события от пользователя id={}", userId);
        User initiator = findUserByIdOrThrow(userId);
        Category category = findCategoryByIdOrThrow(newEventDto.getCategory());
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата начала события должна быть минимум на 2 часа позже текущей даты");
        }
        return EventMapper.toEventFullDto(eventRepository.save(
                EventMapper.fromNewEventDto(newEventDto, initiator, category)));
    }

    @Override
    public List<EventShortDto> privateGetUsersEvents(Long userId, int from, int size) {
        log.info("EventService PRIVATE: поиск всех событий, инициированных пользователем id={}, страница from={}, size={}",
                userId, from, size);
        Pageable pageable = getPageRequest(from, size, null);
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        if (!events.hasContent()) {
            return new ArrayList<>();
        }
        return eventRepository.findAllByInitiatorId(userId, pageable).getContent().stream()
                .map(EventMapper::toEventShortDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("EventService PRIVATE: обновление события с id={} инициатором с id={}", eventId, userId);
        Event event = findEventByIdOrThrow(eventId);
        findUserByIdOrThrow(userId);
        checkInitiator(event, userId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя обновлять опубликованные или отклоненные события");
        }
        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Нельзя обновить событие, так как до его начала осталось меньше 2 часов");
        }
        return EventMapper.toEventFullDto(eventRepository.save(
                updateEvent(event, updateRequest, true)));
    }

    @Override
    public EventFullDto privateFindEventById(Long userId, Long eventId) {
        log.info("EventService PRIVATE: поиск события с id={} от инициатора с id={}", eventId, userId);
        Event event = findEventByIdOrThrow(eventId);
        findUserByIdOrThrow(userId);
        checkInitiator(event, userId);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        String uri = "/event/" + eventId;
        eventFullDto.setViews(getViewsForEvent(List.of(uri)));
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> adminFindAllWithSpecs(List<Long> userIds,
                                                    List<String> states,
                                                    List<Long> categories,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    int from, int size) {
        SearchFilter filter = new SearchFilter();
        filter.setInitiators(userIds);
        if (states != null) {
            List<State> stateList = new ArrayList<>();
            for (String state : states) {
                stateList.add(State.stringToState(state));
            }
            filter.setStates(stateList);
        }
        filter.setCategories(categories);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);
        Pageable pageable = getPageRequest(from, size, null);
        List<Specification<Event>> specifications = eventSpecification.adminGetSpecifications(filter);
        Page<Event> eventPage = eventRepository.findAll(
                specifications.stream().reduce(Specification::or).orElse(null), pageable);
        if (!eventPage.hasContent()) {
            return new ArrayList<>();
        }
        List<Event> events = eventPage.getContent();
        Map<Long, List<Comment>> eventCommentMap = commentRepository.findByEventIn(events).stream()
                .collect(groupingBy(comment -> comment.getEvent().getId(), toList()));

        return eventPage.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .peek(eventDto -> setCommentsToEventFullDto(eventCommentMap, eventDto))
                .collect(toList());
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = findEventByIdOrThrow(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя обновлять опубликованные или отклоненные события");
        }
        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Нельзя обновить событие, так как до его начала осталось меньше 2 часов");
        }
        return EventMapper.toEventFullDto(eventRepository.save(updateEvent(
                event, updateRequest, false)));
    }

    @Override
    public List<EventShortDto> publicFindAllWithSpecs(String text, List<Long> categories, Boolean paid,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable, String sort, int from, int size,
                                                      HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Конец диапазона не может быть раньше начала");
        }
        SearchFilter filter = new SearchFilter();
        filter.setText(text);
        filter.setCategories(categories);
        filter.setPaid(paid);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);
        filter.setOnlyAvailable(onlyAvailable);
        Pageable pageable = getPageRequest(from, size, sort);
        Page<Event> eventPage = eventRepository.findAll(eventSpecification.publicGetSpecifications(filter).stream()
                .reduce(Specification::or).orElse(null), pageable);
        if (!eventPage.hasContent()) {
            return new ArrayList<>();
        }
        List<Event> events = eventPage.getContent();
        Map<Long, List<Comment>> eventCommentMap = commentRepository.findByEventIn(events).stream()
                .collect(groupingBy(comment -> comment.getEvent().getId(), toList()));

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .peek(eventDto -> setComments(eventCommentMap, eventDto))
                .collect(toList());

        setViews(eventShortDtoList);
        createHit(request);
        return eventShortDtoList;
    }

    @Override
    public EventFullDto publicFindById(Long eventId, HttpServletRequest request) {
        Event event = findEventByIdOrThrow(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(
                    String.format("Событие с id=%d еще не опубликовано", eventId));
        }
        List<CommentResultDto> comments = commentRepository.findByEventIn(List.of(event)).stream()
                .map(CommentMapper::toCommentResultDto)
                .collect(toList());
        EventFullDto fullDto = EventMapper.toEventFullDto(event);
        fullDto.setComments(comments);
        String uri = request.getRequestURI();
        fullDto.setViews(getViewsForEvent(List.of(uri)));
        createHit(request);
        return fullDto;
    }

    @Override
    @Transactional
    public CommentResultDto privateCreateComment(NewCommentDto newCommentDto, Long authorId, Long eventId) {
        log.info("EventService: добавление комментария от пользователя с id={} к событию с id={}", authorId, eventId);
        User author = findUserByIdOrThrow(authorId);
        Event event = findEventByIdOrThrow(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя комментировать неопубликованные события");
        }
        return CommentMapper.toCommentResultDto(commentRepository.save(
                CommentMapper.toComment(newCommentDto, author, event)));
    }

    @Override
    @Transactional
    public void privateDeleteComment(Long userId, Long commentId) {
        log.info("EventService: удаление комментария с id={} от пользователя с id={}", commentId, userId);
        Comment comment = findCommentByIdOrThrow(commentId);
        checkUserIsAuthor(comment, userId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void adminDeleteComment(Long commentId) {
        log.info("EventService: удаление комментария с id={} администратором", commentId);
        findCommentByIdOrThrow(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResultDto> privateFindAllUsersComments(Long userId, int from, int size) {
        log.info("EventService: поиск всех комментариев пользователя с id={}", userId);
        User author = findUserByIdOrThrow(userId);
        Pageable pageable = getPageRequest(from, size, null);
        Page<Comment> commentsPage = commentRepository.findByAuthor(author, pageable);
        if (!commentsPage.hasContent()) {
            return new ArrayList<>();
        }
        return commentsPage.getContent().stream()
                .map(CommentMapper::toCommentResultDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentResultDto privateUpdateComment(NewCommentDto updateCommentDto, Long commentId, Long userId) {
        log.info("EventService: обновление комментария с id={} пользователем с id={}", commentId, userId);
        Comment comment = findCommentByIdOrThrow(commentId);
        checkUserIsAuthor(comment, userId);
        comment.setText(updateCommentDto.getText());
        return CommentMapper.toCommentResultDto(commentRepository.save(comment));
    }

    @Override
    public CommentResultDto adminFindCommentById(Long commentId) {
        log.info("EventService: получение комментария с id={} администратором", commentId);
        CommentResultDto comment = CommentMapper.toCommentResultDto(findCommentByIdOrThrow(commentId));
        log.info("Комментарий {} найден", comment);
        return comment;
    }

    private void checkInitiator(Event event, Long userId) {
        log.info("EventService: проверка является ли пользователь с id={} инициатором события с id={}",
                userId, event.getId());
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException(String.format("Пользователь с id=%d не является инициатором события с id=%d",
                    userId, event.getId()));
        }
    }

    private long getViewsForEvent(List<String> uris) {
        log.info("EventService: выполнение метода getViewsForEvent");
        List<EndpointHitsDto> hitsList = statsClient.getHits(LocalDateTime.of(1970, 1, 1, 1, 1, 1),
                LocalDateTime.now(), uris, true);
        if (hitsList.isEmpty()) {
            return 0;
        }
        return hitsList.get(0).getHits();
    }

    private User findUserByIdOrThrow(Long userId) {
        log.info("EventService: поиск пользователя с id={}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "User with id=%d was not found", userId)));
    }

    private Event findEventByIdOrThrow(Long eventId) {
        log.info("EventService: поиск события с id={}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format(
                "Event with id=%d was not found", eventId)));
    }

    private Category findCategoryByIdOrThrow(Long categoryId) {
        log.info("EventService: поиск категории с id={}", categoryId);
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(String.format(
                "Category with id=%d was not found", categoryId)));
    }

    private Event updateEvent(Event event, UpdateEventRequest updateEvent, Boolean isUserRequest) {
        if (updateEvent.getStateAction() != null) {
            if (isUserRequest) {
                handleUserUpdateRequest(event, updateEvent.getStateAction());
            } else {
                handleAdminUpdateRequest(event, updateEvent.getStateAction());
            }
        }
        if (updateEvent.getCategory() != null) {
            event.setCategory(findCategoryByIdOrThrow(updateEvent.getCategory()));
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        return event;
    }

    private void handleUserUpdateRequest(Event event, String stateAction) {
        log.info("EventService: Обновление состояния события инициатором");
        UserStateAction action = UserStateAction.stringToUserAction(stateAction);
        if (action.equals(UserStateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        } else {
            event.setState(State.CANCELED);
        }
    }

    private void handleAdminUpdateRequest(Event event, String stateAction) {
        log.info("EventService: Обновление состояния события админом");
        AdminStateAction action = AdminStateAction.stringToAdminAction(stateAction);
        if (action.equals(AdminStateAction.PUBLISH_EVENT) && event.getState().equals(State.PENDING) &&
                LocalDateTime.now().plusHours(1).isAfter(event.getEventDate())) {
            throw new ForbiddenException("Невозможно опубликовать событие, так как до его начала осталось меньше часа");
        }

        if (action.equals(AdminStateAction.PUBLISH_EVENT) && event.getState().equals(State.PENDING) &&
                LocalDateTime.now().plusHours(1).isBefore(event.getEventDate())) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (action.equals(AdminStateAction.REJECT_EVENT)) {
            event.setState(State.CANCELED);
        } else {
            throw new ForbiddenException("Нет доступных действий по обновлению статуса");
        }
    }

    private PageRequest getPageRequest(int from, int size, String sort) {
        log.info("EventService: выполнение метода getPageRequest");
        int page = from / size;
        Sort sortBy;
        if (sort == null) {
            sortBy = Sort.by(DESC, "id");
        } else {
            if (SortEnum.stringToSort(sort) == SortEnum.EVENT_DATE) {
                sortBy = Sort.by(DESC, "eventDate");
            } else {
                sortBy = Sort.by(DESC, "views");
            }
        }
        return PageRequest.of(page, size, sortBy);
    }

    private void setViews(List<EventShortDto> eventShortDtoList) {
        log.info("EventService: выполнение метода setViews");
        List<String> uris = new ArrayList<>();
        for (EventShortDto dto : eventShortDtoList) {
            uris.add("/events/" + dto.getId());
        }
        List<EndpointHitsDto> hitsDtoList = statsClient.getHits(
                LocalDateTime.of(1970, 1, 1, 1, 1), LocalDateTime.now(), uris, true);
        Map<Long, EventShortDto> eventShortDtoMap = eventShortDtoList.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));

        for (EndpointHitsDto dto : hitsDtoList) {
            Long id = Long.parseLong(dto.getUri().substring(8));
            eventShortDtoMap.get(id).setViews(dto.getHits());
        }
    }

    private void createHit(HttpServletRequest request) {
        log.info("EventService: выполнение метода createHit");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        EndpointRequestDto requestDto = new EndpointRequestDto();
        requestDto.setUri(request.getRequestURI());
        requestDto.setIp(request.getRemoteAddr());
        requestDto.setApp("ewm-main-service");
        requestDto.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        statsClient.create(requestDto);
    }

    private void setComments(Map<Long, List<Comment>> commentMap, EventShortDto eventShortDto) {
        log.info("EventService: добавление комментариев к событию с id={}", eventShortDto.getId());
        if (commentMap.isEmpty()) {
            return;
        }
        if (commentMap.get(eventShortDto.getId()) == null) {
            return;
        }
        List<CommentResultDto> eventComments = commentMap.get(eventShortDto.getId()).stream()
                .map(CommentMapper::toCommentResultDto)
                .collect(toList());
        eventShortDto.setComments(eventComments);
    }

    private void setCommentsToEventFullDto(Map<Long, List<Comment>> commentMap, EventFullDto eventFullDto) {
        log.info("EventService: добавление комментариев к событию с id={}", eventFullDto.getId());
        if (commentMap.isEmpty()) {
            return;
        }
        if (commentMap.get(eventFullDto.getId()) == null) {
            return;
        }
        List<CommentResultDto> eventComments = commentMap.get(eventFullDto.getId()).stream()
                .map(CommentMapper::toCommentResultDto)
                .collect(toList());
        eventFullDto.setComments(eventComments);
    }

    private Comment findCommentByIdOrThrow(Long commentId) {
        log.info("EventService: поиск комментария с id={}", commentId);
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format(
                "Comment with id=%d was not found", commentId)));
    }

    private void checkUserIsAuthor(Comment comment, Long userId) {
        log.info("EventService: проверка является ли пользователь с id={} автором комментария с id={}",
                userId, comment.getId());
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException(String.format(
                    "Пользователь с id=%d не является автором комментария с id=%d", userId, comment.getId()));
        }
    }
}
