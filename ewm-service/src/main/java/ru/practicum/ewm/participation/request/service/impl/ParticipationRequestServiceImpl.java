package ru.practicum.ewm.participation.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.enums.State;
import ru.practicum.ewm.event.repository.JpaEventRepository;
import ru.practicum.ewm.exception.exceptions.ConflictException;
import ru.practicum.ewm.exception.exceptions.NotFoundException;
import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.participation.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participation.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.request.dto.mapper.RequestMapper;
import ru.practicum.ewm.participation.request.model.ParticipationRequest;
import ru.practicum.ewm.participation.request.model.enums.ParticipationStatus;
import ru.practicum.ewm.participation.request.repository.JpaParticipationRequestRepository;
import ru.practicum.ewm.participation.request.service.ParticipationRequestService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.JpaUserRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final JpaParticipationRequestRepository requestRepository;
    private final JpaUserRepository userRepository;
    private final JpaEventRepository eventRepository;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "id");

    @Override
    public List<ParticipationRequestDto> findByRequester(Long userId) {
        log.info("ParticipationRequestService: Поиск всех participation request пользователя с id={}", userId);
        findUserByIdOrThrow(userId);
        return requestRepository.findAllByRequesterId(userId, sort).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("ParticipationRequestService: запрос от пользователя с id={} на участие в мероприятии с id={}",
                userId, eventId);
        User requester = findUserByIdOrThrow(userId);
        Event event = findEventByIdOrThrow(eventId);
        if (event.getInitiator().equals(requester)) {
            throw new ConflictException("Инициатор события не может подать заявку на участие в нем");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Нет свободных мест для участия в событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя добавить запрос на участив в неопубликованном событии");
        }
        Optional<ParticipationRequest> checkRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (checkRequest.isPresent()) {
            throw new ConflictException(
                    String.format("Запрос от пользователя с id=%d на участие в событии с id=%d уже создан", userId, eventId
                    ));
        }
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setStatus(ParticipationStatus.PENDING);
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            request.setStatus(ParticipationStatus.CONFIRMED);
            eventRepository.save(event);
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = findRequestOrThrow(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException(String.format("Пользователь с id=%d не является создателем запроса с id=%d",
                    userId, requestId));
        }
        if (request.getStatus().equals(ParticipationStatus.CONFIRMED)) {
            Event event = findEventByIdOrThrow(request.getEvent().getId());
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setStatus(ParticipationStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findAllByEventAndInitiator(Long userId, Long eventId) {
        findUserByIdOrThrow(userId);
        Event event = findEventByIdOrThrow(eventId);
        checkIfUserIsEventInitiator(userId, event);
        return requestRepository.findAllByEventId(eventId, sort).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        findUserByIdOrThrow(userId);
        Event event = findEventByIdOrThrow(eventId);
        checkIfUserIsEventInitiator(userId, event);
        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            throw new ConflictException(
                    String.format("Заявки на участие в событии с id=%d не требуют модерации", eventId));
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        EventRequestStatusUpdateResult result = updateStatus(requests, updateRequest.getStatus(), event);
        requestRepository.saveAll(requests);
        eventRepository.save(event);
        return result;
    }

    private EventRequestStatusUpdateResult updateStatus(List<ParticipationRequest> requests,
                                                        ParticipationStatus status,
                                                        Event event) {
        log.info("ParticipationRequestService: перевод запросов на участие в статус {}", status);
        int limit = event.getParticipantLimit();
        int confirmed = event.getConfirmedRequests();
        Iterator<ParticipationRequest> iterator = requests.iterator();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (limit == confirmed) {
            throw new ConflictException("Лимит заявок для события исчерпан");
        }

        while (limit > confirmed && iterator.hasNext()) {
            ParticipationRequest request = iterator.next();
            if (!request.getStatus().equals(ParticipationStatus.PENDING)) {
                throw new ConflictException("Заявка уже была рассмотрена");
            }
            request.setStatus(status);

            if (status.equals(ParticipationStatus.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                confirmed++;
            } else {
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }
        }

        while (iterator.hasNext()) {
            ParticipationRequest request = iterator.next();
            request.setStatus(ParticipationStatus.REJECTED);
            rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        event.setConfirmedRequests(confirmed);
        return result;
    }

    private void checkIfUserIsEventInitiator(Long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format(
                    "Пользователь с id=%d не является инициатором события с id=%d", userId, event.getId()
            ));
        }
    }

    private User findUserByIdOrThrow(Long userId) {
        log.info("ParticipationRequestService: поиск пользователя с id={}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "User with id=%d was not found", userId)));
    }

    private Event findEventByIdOrThrow(Long eventId) {
        log.info("ParticipationRequestService: поиск события с id={}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format(
                "Event with id=%d was not found", eventId)));
    }

    private ParticipationRequest findRequestOrThrow(Long requestId) {
        log.info("ParticipationRequestService: поиск запроса на участие с id={}", requestId);
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format(
                "Request with id=%d was not found", requestId)));
    }
}
