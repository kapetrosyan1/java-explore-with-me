package ru.practicum.ewm.compilation.dto.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CompilationMapper {
    public static Compilation fromNewCompDto(NewCompilationDto newCompilationDto, Set<Event> eventSet) {
        log.info("CompilationMapper: конвертация NewCompilationDto в Compilation");
        Compilation compilation = new Compilation();
        compilation.setEvents(eventSet);
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        log.info("CompilationMapper: конвертация Compilation в CompilationDto");
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList())
        );
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }
}
