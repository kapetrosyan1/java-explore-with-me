package ru.practicum.ewm.compilation.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("PublicCompilationController: GET запрос по endpoint /compilations");
        return compilationService.publicFindCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable @Positive Long compId) {
        log.info("PublicCompilationController: GET запрос по endpoint /compilations/{}", compId);
        return compilationService.publicFindCompilationById(compId);
    }
}
