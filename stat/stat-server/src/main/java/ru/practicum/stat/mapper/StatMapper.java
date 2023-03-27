package ru.practicum.stat.mapper;

import ru.practicum.ewn.dto.ViewStatDto;
import ru.practicum.stat.model.Stat;

public class StatMapper {

    public static ViewStatDto toStatDto(Stat stat) {
        return new ViewStatDto(
                stat.getApp(),
                stat.getUri(),
                stat.getHits()
        );
    }

    public static Stat toStat(ViewStatDto viewStatDto) {
        return new Stat(
                viewStatDto.getApp(),
                viewStatDto.getUri(),
                viewStatDto.getHits()
        );
    }
}
