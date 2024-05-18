package ru.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.dto.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.JpaCompilationRepository;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.JpaEventRepository;
import ru.practicum.ewm.exception.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final JpaCompilationRepository compilationRepository;
    private final JpaEventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto adminCreateCompilation(NewCompilationDto newCompilationDto) {
        log.info("CompilationService ADMIN: создание новой подборки событий");
        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
            if (newCompilationDto.getEvents().size() > events.size()) {
                log.warn("WARNING: количество найденных событий меньше, чем запрашивалось");
            }
        }
        return CompilationMapper.toCompilationDto(
                compilationRepository.save(CompilationMapper.fromNewCompDto(newCompilationDto, events)));
    }

    @Override
    @Transactional
    public void adminDeleteCompilation(Long compId) {
        log.info("CompilationService ADMIN: удаление подборки событий с id={}", compId);
        findCompOrThrow(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto adminUpdateCompilation(UpdateCompilationRequest request, Long compId) {
        log.info("CompilationService ADMIN: обновление подборки событий с id={}", compId);
        Compilation compilation = findCompOrThrow(compId);
        updateCompilation(compilation, request);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> publicFindCompilations(Boolean pinned, int from, int size) {
        log.info("CompilationService PUBLIC: получение подборки событий, pinned={}, from={}, size={}", pinned, from, size);
        int page = from / size;
        Sort sort = Sort.by(ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Compilation> compilationPage;
        if (pinned == null) {
            compilationPage = compilationRepository.findAll(pageable);
        } else {
            compilationPage = compilationRepository.findAllByPinned(pinned, pageable);
        }
        if (!compilationPage.hasContent()) {
            return new ArrayList<>();
        }
        return compilationPage.getContent().stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto publicFindCompilationById(Long compId) {
        log.info("CompilationService PUBLIC: получение подборки событий с id={}", compId);
        Compilation compilation = findCompOrThrow(compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    private Compilation findCompOrThrow(Long compId) {
        log.info("CompilationService: проверяется есть ли запрашиваемая подборка");
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                String.format("Компиляция с id=%d не найдена", compId)
        ));
    }

    private void updateCompilation(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        log.info("выполняется метод private void updateCompilation");
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            log.info("Заменяются события в подборке");
            Set<Event> updEvents = new HashSet<>(
                    eventRepository.findAllById(updateCompilationRequest.getEvents())
            );
            compilation.setEvents(updEvents);
        }
        if (updateCompilationRequest.getPinned() != null) {
            log.info("Новый статус pinned для подборки с id={}: pinned={}",
                    compilation.getId(), updateCompilationRequest.getPinned());
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            log.info("Новый title для подборки с id={}: title={}",
                    compilation.getId(), updateCompilationRequest.getTitle());
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
    }
}
