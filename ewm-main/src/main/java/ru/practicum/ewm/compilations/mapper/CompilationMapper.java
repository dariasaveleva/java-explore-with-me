package ru.practicum.ewm.compilations.mapper;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.ResponseCompilationDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(CompilationDto compilationDto, List<Event> events) {
        return new Compilation(null,
                events,
                compilationDto.getPinned(),
                compilationDto.getTitle()
        );
    }

    public static ResponseCompilationDto toResponseCompilationDto(Compilation compilation) {
        List<ShortEventDto> events = compilation.getEvents().stream().map(EventMapper::toShortEventDto).collect(Collectors.toList());
        return new ResponseCompilationDto(
                compilation.getId(),
                events,
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
