package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.ViewStatDto;
import ru.practicum.ewm.model.Stat;

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
