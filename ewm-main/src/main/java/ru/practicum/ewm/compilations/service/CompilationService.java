package ru.practicum.ewm.compilations.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.ResponseCompilationDto;

import java.util.List;

public interface CompilationService {
     List<ResponseCompilationDto> findAllCompilations(Boolean pinned, Pageable page);

     ResponseCompilationDto findCompilationById(Long compId);

     ResponseCompilationDto createCompilation(CompilationDto compilationDto);

     void deleteCompilation(Long compId);

     ResponseCompilationDto updateCompilation(Long compId, CompilationDto compilationDto);
}
