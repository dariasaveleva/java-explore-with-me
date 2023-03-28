package ru.practicum.stat.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    EndpointHitDto create(EndpointHitDto endpointHitDto);
}
