package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.mapper.StatMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
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
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getUniqueStatsWithoutUri(start, end).stream()
                        .map(StatMapper::toStatDto).collect(Collectors.toList());
            } else {
                return repository.getNotUniqueStatsWithoutUri(start, end).stream()
                        .map(StatMapper::toStatDto).collect(Collectors.toList());
            }

        } else if (unique) {
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
