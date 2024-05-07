package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto adminCreateCompilation(NewCompilationDto newCompilationDto);

    void adminDeleteCompilation(Long compId);

    CompilationDto adminUpdateCompilation(UpdateCompilationRequest request, Long compId);

    List<CompilationDto> publicFindCompilations(Boolean pinned, int from, int size);

    CompilationDto publicFindCompilationById(Long compId);
}
