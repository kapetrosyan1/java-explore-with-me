package ru.practicum.ewm.compilation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("AdminCompilationController: POST запрос по endpoint /admin/compilations");
        return compilationService.adminCreateCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        log.info("AdminCompilationController: DELETE запрос по endpoint /admin/compilations/{}", compId);
        compilationService.adminDeleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationRequest request,
                                 @PathVariable @Positive Long compId) {
        log.info("AdminCompilationController: PATCH запрос по endpoint /admin/compilations/{}", compId);
        return compilationService.adminUpdateCompilation(request, compId);
    }
}
