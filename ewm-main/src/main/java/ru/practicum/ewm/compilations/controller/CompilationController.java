package ru.practicum.ewm.compilations.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilations.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RequestMapping()
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationController {
    final CompilationService service;
    final String adminUrl = "/admin/compilations";
    static final String publicUrl = "/compilations";

    @GetMapping(publicUrl)
    public List<ResponseCompilationDto> findAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                            @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return service.findAllCompilations(pinned, page);
    }

    @GetMapping(publicUrl + "/{compId}")
    public ResponseCompilationDto findCompilationById(@PathVariable Long compId) {
        return service.findCompilationById(compId);
    }

    @PostMapping(adminUrl)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCompilationDto createCompilation(@Valid @RequestBody CompilationDto compilationDto) {
        return service.createCompilation(compilationDto);
    }

    @DeleteMapping(adminUrl + "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping(adminUrl + "/{compId}")
    public ResponseCompilationDto updateCompilation(@PathVariable Long compId, @RequestBody CompilationDto compilationDto) {
        return service.updateCompilation(compId, compilationDto);
    }

}
