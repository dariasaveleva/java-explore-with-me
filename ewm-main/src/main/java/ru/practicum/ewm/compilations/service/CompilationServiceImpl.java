package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationsRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ResponseCompilationDto> findAllCompilations(Boolean pinned, Pageable page) {
        return compilationsRepository.findAllByPinned(pinned, page).stream()
                .map(CompilationMapper::toResponseCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCompilationDto findCompilationById(Long compId) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow();
        return CompilationMapper.toResponseCompilationDto(compilation);
    }

    @Override
    @Transactional
    public ResponseCompilationDto createCompilation(CompilationDto compilationDto) {
        List<Event> events = eventRepository.findByIds(compilationDto.getEvents());
        log.info("Создана новая подборка с названием {}", compilationDto.getTitle());
        return CompilationMapper.toResponseCompilationDto(compilationsRepository
                        .save(CompilationMapper.toCompilation(compilationDto, events)));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationsRepository.findById(compId).orElseThrow();
        compilationsRepository.deleteById(compId);
        log.info("Подборка с id {} удалена", compId);
    }

    @Override
    @Transactional
    public ResponseCompilationDto updateCompilation(Long compId, CompilationDto compilationDto) {
        Compilation compilation = compilationsRepository.findById(compId).orElseThrow();
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIds(compilationDto.getEvents()));
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        compilationsRepository.save(compilation);
        log.info("Подборка с id {} обновлена", compilation.getId());
        return CompilationMapper.toResponseCompilationDto(compilation);
    }
}
