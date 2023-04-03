package ru.practicum.ewm.events.mapper;

import ru.practicum.ewm.categories.mapper.CategoryMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventDtoCreation;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    public static Event toEvent(User initiator, Category category, EventDtoCreation eventDtoCreation) {
        return new Event(
                null,
        eventDtoCreation.getAnnotation(),
        category,
        LocalDateTime.now(),
        eventDtoCreation.getDescription(),
        eventDtoCreation.getEventDate(),
        initiator,
        eventDtoCreation.getLocation(),
        eventDtoCreation.getPaid(),
        eventDtoCreation.getParticipantLimit(),
        LocalDateTime.now(),
        eventDtoCreation.getRequestModeration(),
        EventState.PENDING,
        eventDtoCreation.getTitle()
        );
    }

    public static EventDtoCreation toEventDtoCreation(Event event) {
        return new EventDtoCreation(
                event.getAnnotation(),
                event.getCategory().getId(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getTitle()
        );
    }

    public static EventDto toEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                null,
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getDescription(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getRequestModeration(),
                event.getEventState().toString(),
                event.getTitle(),
                0L
        );
    }

    public static ShortEventDto toShortFromEventDto(EventDto dto) {
        return new ShortEventDto(
                dto.getId(),
        dto.getAnnotation(),
        dto.getCategory(),
        dto.getConfirmedRequests(),
        dto.getEventDate(),
        dto.getInitiator(),
        dto.getPaid(),
        dto.getTitle(),
        dto.getViews()
        );
    }

    public static ShortEventDto toShortEventDto(Event event) {
        return new ShortEventDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                null,
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0L
        );
    }
}
