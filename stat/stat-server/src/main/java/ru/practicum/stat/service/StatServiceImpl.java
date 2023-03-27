package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewn.dto.EndpointHitDto;
import ru.practicum.ewn.dto.ViewStatDto;
import ru.practicum.stat.mapper.EndpointHitMapper;
import ru.practicum.stat.mapper.StatMapper;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatServiceImpl implements StatService {
    private final StatRepository repository;


    @Override
    public List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Статистика найдена");
        if (uris.isEmpty() || uris == null) return Collections.emptyList();
        if (unique) {
             return repository.getUniqueStat(start, end, uris).stream()
                     .map(StatMapper::toStatDto)
                     .collect(Collectors.toList());
        } else {
            return repository.getNotUniqueStat(start, end, uris).stream()
                    .map(StatMapper::toStatDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = repository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("Запрос сохранён с id {}", endpointHit.getId());
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }
}
